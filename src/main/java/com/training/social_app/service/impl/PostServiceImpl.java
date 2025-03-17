package com.training.social_app.service.impl;

import com.training.social_app.entity.Post;
import com.training.social_app.entity.User;
import com.training.social_app.repository.PostRepository;
import com.training.social_app.repository.UserRepository;
import com.training.social_app.service.PostService;
import com.training.social_app.utils.UserContext;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;

    @Autowired
    private final UserRepository userRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    private Integer getCurrentUserId() {
        User currentUser = userRepository.findById(UserContext.getUser().getUser().getId())
                .orElseThrow(() -> new EntityNotFoundException("Current user not found"));
        return currentUser.getId();
    }

    @Override
    public int countPostsForUserInPastWeek() {
        Integer userId = getCurrentUserId();
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, cal.getWeeksInWeekYear());
        if(cal.getFirstDayOfWeek() != Calendar.MONDAY){
            cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        }
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        LocalDate startDate = LocalDate.of(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        LocalDate endDate = LocalDate.now();
        return postRepository.countByUserIdAndCreatedAtBetween(userId, startDate, endDate);
    }

    @Override
    public List<Post> getPostsByUserId() {
        Integer userId = getCurrentUserId();
        List<Post> postList= postRepository.findAllByUserId(userId);
        if(postList.isEmpty()){
            throw new RuntimeException("No posts found for user: " + UserContext.getUser().getUsername());
        }
        return postList;
    }

//    //Cap nhat lai phan get friend id sau khi da co crud cua friendship
//    @Override
//    public List<Post> getPostsOfFriendsSortedByDate(List<Integer> friendIds) {
//        return postRepository.findByUserIdIn(friendIds, Sort.by(Sort.Direction.DESC, "createdDate"));
//    }

    @Override
    public Post createPost(String content, MultipartFile file) {
        Integer userId = getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found for id: " + userId));
        Post newPost = new Post();
        newPost.setUser(user);
        newPost.setContent(content);
        if (file != null && !file.isEmpty()) {
            // Validate file type
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new RuntimeException("Only image files are allowed");
            }
            try {
                String fileName = userId + "_" + file.getOriginalFilename();
                Path filePath = Paths.get(uploadDir, fileName);
                Files.createDirectories(filePath.getParent());
                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                newPost.setImageUrl(filePath.toString());
            } catch (IOException e) {
                throw new RuntimeException("Failed to store file", e);
            }
        }
        return postRepository.save(newPost);
    }

    @Override
    public Post updatePost(String content, MultipartFile file ,Integer postId) {
        Integer userId = getCurrentUserId();
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found for id: " + postId));
        if (!post.getUser().getId().equals(userId)) {
            throw new RuntimeException("You are not allowed to update this post");
        }
        post.setContent(content);
        if (file != null && !file.isEmpty()) {
            // Validate file type
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new RuntimeException("Only image files are allowed");
            }
            try {
                String fileName = userId + "_" + file.getOriginalFilename();
                Path filePath = Paths.get(uploadDir, fileName);
                Files.createDirectories(filePath.getParent());
                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                post.setImageUrl(filePath.toString());
            } catch (IOException e) {
                throw new RuntimeException("Failed to store file", e);
            }
        }
        post.setUpdatedAt(LocalDateTime.now());
        return postRepository.save(post);
    }


    @Override
    public void deletePost(Integer postId) {
        Integer userId = getCurrentUserId();
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found for id: " + postId));
        if (!post.getUser().getId().equals(userId)) {
            throw new RuntimeException("You are not allowed to delete this post");
        }
        postRepository.delete(post);
    }
}
