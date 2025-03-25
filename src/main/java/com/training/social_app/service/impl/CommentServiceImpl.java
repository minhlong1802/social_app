package com.training.social_app.service.impl;

import com.training.social_app.dto.request.CommentRequest;
import com.training.social_app.entity.Comment;
import com.training.social_app.entity.User;
import com.training.social_app.exception.UserForbiddenException;
import com.training.social_app.repository.CommentRepository;
import com.training.social_app.repository.PostRepository;
import com.training.social_app.repository.UserRepository;
import com.training.social_app.service.CommentService;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final PostRepository postRepository;

    private Integer getCurrentUserId() {
        User currentUser = userRepository.findById(UserContext.getUser().getUser().getId())
                .orElseThrow(() -> new EntityNotFoundException("Current user not found"));
        return currentUser.getId();
    }

    @Override
    public Comment commentPost(CommentRequest commentRequest) {
        Integer userId = getCurrentUserId();
        //Handle validation
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found for id: " + userId));
        //Create a new comment
        Comment comment = new Comment();
        comment.setPost(postRepository.findById(commentRequest.getPostId()).orElseThrow(() -> new EntityNotFoundException("Post not found for id: " + commentRequest.getPostId())));
        comment.setUser(user);
        comment.setContent(commentRequest.getContent());
        return commentRepository.save(comment);
    }

    @Override
    public Comment editComment(CommentRequest commentRequest,Integer commentId ) {
        Integer userId = getCurrentUserId();
        //Handle validation
        Comment existingComment = commentRepository.findById(commentId).orElseThrow(() -> new EntityNotFoundException("Comment not found for id: " + commentId));
        if (!existingComment.getUser().getId().equals(userId)) {
            throw new UserForbiddenException("You are not allowed to update this comment");
        }
        if(commentRequest.getPostId() != null){
            throw new RuntimeException("Cannot update this field")        ;
        }
        existingComment.setContent(commentRequest.getContent());
        existingComment.setUpdatedAt(LocalDateTime.now());
        return commentRepository.save(existingComment);
    }

    @Override
    public void deleteComment(Integer commentId) {
        Integer userId = getCurrentUserId();
        //Handle validation
        Comment existingComment = commentRepository.findById(commentId).orElseThrow(() -> new EntityNotFoundException("Comment not found for id: " + commentId));
        if (!existingComment.getUser().getId().equals(userId)) {
            throw new UserForbiddenException("You are not allowed to delete this comment");
        }
        commentRepository.delete(existingComment);
    }

    @Override
    public List<Comment> getCommentsByPostId(Integer postId, Integer page, Integer size) {
        if (postRepository.findById(postId).isEmpty()) {
            throw new EntityNotFoundException("Post not found for id: " + postId);
        }
        if (page > 0) {
            page = page - 1;
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<Comment> pageComments = commentRepository.findAllByPostId(postId, pageable);
        return pageComments.getContent();
    }

    @Override
    public Comment getCommentById(Integer commentId) {
        return commentRepository.findById(commentId).orElseThrow(() -> new EntityNotFoundException("Comment not found for id: " + commentId));
    }
}
