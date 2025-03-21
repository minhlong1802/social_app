package com.training.social_app.service.impl;

import com.training.social_app.dto.request.DeleteRequest;
import com.training.social_app.dto.response.PostResponse;
import com.training.social_app.entity.Post;
import com.training.social_app.entity.User;
import com.training.social_app.enums.Role;
import com.training.social_app.repository.*;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final FriendShipRepository friendShipRepository;

    @Autowired
    private final LikeRepository likeRepository;

    @Autowired
    private final CommentRepository commentRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    private Integer getCurrentUserId() {
        User currentUser = userRepository.findById(UserContext.getUser().getUser().getId())
                .orElseThrow(() -> new EntityNotFoundException("Current user not found"));
        return currentUser.getId();
    }

    private PostResponse convertToDTO(Post post) {
        PostResponse postDTO = new PostResponse();
        postDTO.setId(post.getId());
        postDTO.setContent(post.getContent());
        postDTO.setImageUrl(post.getImageUrl());
        postDTO.setUserId(post.getUser().getId());
        postDTO.setEdited(true);
        postDTO.setLikeCount(likeRepository.countLikesByPostId(post.getId()));
        postDTO.setCommentCount(commentRepository.countCommentsByPostId(post.getId()));
        return postDTO;
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
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = LocalDateTime.now();
        return postRepository.countByUserIdAndCreatedAtBetween(userId, startDateTime, endDateTime);
    }

    @Override
    public List<PostResponse> getPostsByUserId() {
        Integer userId = getCurrentUserId();
        List<Post> postList = postRepository.findAllByUserId(userId);
        if (postList.isEmpty()) {
            throw new RuntimeException("No posts found for user: " + UserContext.getUser().getUsername());
        }
        return postList.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public List<PostResponse> getPostsOfFriendsSortedByDate() {
        Integer userId = getCurrentUserId();
        List<User> friends = friendShipRepository.findFriendsByUserId(userId);
        List<Integer> friendIds = friends.stream().map(User::getId).collect(Collectors.toList());
        List<Post> posts = postRepository.findByUserIdIn(friendIds, Sort.by(Sort.Direction.DESC, "createdAt"));
        return posts.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public Post createPost(String content, MultipartFile file) {
        Integer userId = getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found for id: " + userId));
        Post newPost = new Post();
        newPost.setUser(user);

        if ((content == null || content.isEmpty()) && (file == null || file.isEmpty())) {
            throw new RuntimeException("Post cannot be empty");
        }

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
        if ((content == null || content.isEmpty()) && (file == null || file.isEmpty())) {
            throw new RuntimeException("Post cannot be empty");
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
        post.setIsEdited(true);
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

    @Override
    public List<PostResponse> findAll() {
        User user = userRepository.findById(UserContext.getUser().getUser().getId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        if (!user.getRole().equals(Role.ADMIN)) {
            throw new RuntimeException("User is not authorized to see all posts");
        }
        return postRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public void deletePosts(DeleteRequest request){
        User user = userRepository.findById(UserContext.getUser().getUser().getId()).orElseThrow(() -> new EntityNotFoundException("User not found"));
        if (!user.getRole().equals(Role.ADMIN)) {
            throw new RuntimeException("User is not authorized to delete posts");
        }

        List<Integer> ids = request.getIds();
        List<Post> postsToDelete = postRepository.findAllById(ids);

        List<Integer> existingIds = postsToDelete.stream()
                .map(Post::getId)
                .toList();

        List<Integer> notFoundIds = ids.stream()
                .filter(id -> !existingIds.contains(id))
                .toList();

        if (!notFoundIds.isEmpty()) {
            throw new EntityNotFoundException("Posts not found for ids: " + notFoundIds);
        }

        postRepository.deleteAll(postsToDelete);
    }

    @Override
    public PostResponse findById(Integer postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found for id: " + postId));
        return convertToDTO(post);
    }
}
