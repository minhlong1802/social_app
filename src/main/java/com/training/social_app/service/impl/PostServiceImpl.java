package com.training.social_app.service.impl;

import com.training.social_app.dto.request.DeleteRequest;
import com.training.social_app.dto.response.PostResponse;
import com.training.social_app.entity.Post;
import com.training.social_app.entity.User;
import com.training.social_app.enums.Role;
import com.training.social_app.exception.UserForbiddenException;
import com.training.social_app.repository.*;
import com.training.social_app.service.PostService;
import com.training.social_app.utils.UserContext;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
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
        postDTO.setCreatedAt(post.getCreatedAt());
        postDTO.setUpdatedAt(post.getUpdatedAt());
        return postDTO;
    }

    @Override
    public List<PostResponse> getPostsByUserId(Integer page, Integer size) {
        Integer userId = getCurrentUserId();
        if (page > 0) {
            page = page - 1;
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> postPage = postRepository.findAllByUserId(userId, pageable);
        return postPage.getContent().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public List<PostResponse> getPostsOfFriendsSortedByDate(Integer page, Integer size) {
        Integer userId = getCurrentUserId();
        List<User> friends = friendShipRepository.getFriendsByUserId(userId);
        List<Integer> friendIds = friends.stream().map(User::getId).collect(Collectors.toList());

        if (page > 0) {
            page = page - 1;
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt"));
        Page<Post> pagePosts = postRepository.findByUserIdIn(friendIds, pageable);

        return pagePosts.getContent().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public PostResponse createPost(String content, MultipartFile file) {
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
        return convertToDTO(postRepository.save(newPost));
    }

    @Override
    public PostResponse updatePost(String content, MultipartFile file ,Integer postId) {
        Integer userId = getCurrentUserId();
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found for id: " + postId));
        if (!post.getUser().getId().equals(userId)) {
            throw new UserForbiddenException("You are not allowed to update this post");
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

        return convertToDTO(postRepository.save(post));
    }


    @Override
    public void deletePost(Integer postId) {
        Integer userId = getCurrentUserId();
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found for id: " + postId));
        if (!post.getUser().getId().equals(userId)) {
            throw new UserForbiddenException("You are not allowed to delete this post");
        }
        postRepository.delete(post);
    }

    @Override
    public List<PostResponse> findAll(Integer page, Integer size) {
        User user = userRepository.findById(UserContext.getUser().getUser().getId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        if (!user.getRole().equals(Role.ADMIN)) {
            throw new UserForbiddenException("You are not allowed to see all posts");
        }
        if (page > 0) {
            page = page - 1;
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> pagePosts = postRepository.findAll(pageable);
        return pagePosts.getContent().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public void deletePosts(DeleteRequest request){
        User user = userRepository.findById(UserContext.getUser().getUser().getId()).orElseThrow(() -> new EntityNotFoundException("User not found"));
        if (!user.getRole().equals(Role.ADMIN)) {
            throw new UserForbiddenException("User is not authorized to delete posts");
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
