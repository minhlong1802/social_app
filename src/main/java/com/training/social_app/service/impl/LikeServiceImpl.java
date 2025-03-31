package com.training.social_app.service.impl;

import com.training.social_app.dto.response.LikeResponse;
import com.training.social_app.entity.Like;
import com.training.social_app.entity.Post;
import com.training.social_app.entity.User;
import com.training.social_app.repository.LikeRepository;
import com.training.social_app.repository.PostRepository;
import com.training.social_app.repository.UserRepository;
import com.training.social_app.service.LikeService;
import com.training.social_app.utils.UserContext;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LikeServiceImpl implements LikeService {
    private final LikeRepository likeRepository;

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final PostRepository postRepository;

    private Integer getCurrentUserId() {
        User currentUser = userRepository.findById(UserContext.getUser().getUser().getId())
                .orElseThrow(() -> new EntityNotFoundException("Current user not found"));
        return currentUser.getId();
    }

    private LikeResponse convertToDTO(Like like) {
        LikeResponse likeResponse = new LikeResponse();
        likeResponse.setId(like.getId());
        likeResponse.setPostId(like.getPost().getId());
        likeResponse.setUserId(like.getUser().getId());
        likeResponse.setUserFullName(like.getUser().getUserProfile().getFullName());
        likeResponse.setUserProfileImage(like.getUser().getUserProfile().getAvatarUrl());
        likeResponse.setCreatedAt(like.getCreatedAt());
        return likeResponse;
    }

    @Override
    public LikeResponse likePost(Integer postId) {
        Integer userId = getCurrentUserId();
        // Handle validation
        Post post = postRepository.findById(postId).orElseThrow(() -> new EntityNotFoundException("Post not found for id: " + postId));
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found for id: " + userId));

        // Check if the like already exists
        Optional<Like> existingLike = likeRepository.findByUserIdAndPostId(userId, postId);
        if (existingLike.isPresent()) {
            likeRepository.delete(existingLike.get());
            return convertToDTO(existingLike.get());
        }

        // Create a new like
        Like like = new Like();
        like.setPost(post);
        like.setUser(user);
        return convertToDTO(likeRepository.save(like)) ;
    }

    @Override
    public List<LikeResponse> getLikesForPost(Integer postId, Integer page, Integer size) {
        Optional<Post> post = postRepository.findById(postId);
        if(post.isEmpty()) {
            throw new EntityNotFoundException("Post not found for id: " + postId);
        }
        try {
            if (page > 0) {
                page = page - 1;
            }
            Pageable pageable = PageRequest.of(page, size);
            Page<Like> pageLikes = likeRepository.findByPostId(postId, pageable);
            return pageLikes.getContent().stream().map(this::convertToDTO).collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public LikeResponse getLikeById(Integer likeId) {
        Like like = likeRepository.findById(likeId).orElseThrow(() -> new EntityNotFoundException("Like not found for id: " + likeId));
        return convertToDTO(like);
    }
}
