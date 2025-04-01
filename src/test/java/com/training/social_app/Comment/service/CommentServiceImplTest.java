package com.training.social_app.Comment.service;

import com.training.social_app.dto.request.CommentRequest;
import com.training.social_app.dto.response.CommentResponse;
import com.training.social_app.dto.response.UserDto;
import com.training.social_app.entity.Comment;
import com.training.social_app.entity.Post;
import com.training.social_app.entity.User;
import com.training.social_app.entity.UserProfile;
import com.training.social_app.exception.UserForbiddenException;
import com.training.social_app.repository.CommentRepository;
import com.training.social_app.repository.PostRepository;
import com.training.social_app.repository.UserRepository;
import com.training.social_app.service.impl.CommentServiceImpl;
import com.training.social_app.utils.UserContext;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentServiceImplTest {
    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private CommentServiceImpl commentService;

    private Comment comment;

    private static MockedStatic<UserContext> mockedUserContext;

    @BeforeAll
    static void init() {
        mockedUserContext = mockStatic(UserContext.class);
    }

    @AfterAll
    static void tearDown() {
        mockedUserContext.close();
    }

    @BeforeEach
    void setUp() {
        User mockUser = new User();
        mockUser.setId(1);
        UserDto mockUserDto = new UserDto(mockUser);

        mockedUserContext.when(UserContext::getUser).thenReturn(mockUserDto);

        // Use lenient() to prevent UnnecessaryStubbingException
        lenient().when(userRepository.findById(1)).thenReturn(Optional.of(mockUser));

        Post post = new Post();
        post.setId(1);

        comment = new Comment();
        comment.setId(1);
        comment.setUser(mockUser);
        comment.setPost(post);
        comment.setContent("Test Comment");
        comment.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void commentPost_ValidRequest_ShouldReturnCommentResponse() {
        int userId = 1;
        int postId = 2;
        String content = "This is a test comment";

        User user = new User();
        user.setId(userId);
        UserProfile userProfile = new UserProfile();
        userProfile.setFullName("John Doe");
        userProfile.setAvatarUrl("avatar.jpg");
        user.setUserProfile(userProfile);

        Post post = new Post();
        post.setId(postId);

        Comment comment = new Comment();
        comment.setId(1);
        comment.setPost(post);
        comment.setUser(user);
        comment.setContent(content);

        CommentRequest request = new CommentRequest();
        request.setPostId(postId);
        request.setContent(content);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentResponse response = commentService.commentPost(request);

        assertNotNull(response);
        assertEquals(1, response.getId());
        assertEquals(userId, response.getUserId());
        assertEquals(postId, response.getPostId());
        assertEquals(content, response.getContent());

        verify(postRepository).findById(postId);
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void editComment_PostIdProvided_ShouldThrowException() {
        Integer userId = 1;
        Integer commentId = 10;

        CommentRequest commentRequest = new CommentRequest();
        commentRequest.setPostId(2); // This should trigger the exception
        commentRequest.setContent("Updated content");

        Comment existingComment = new Comment();
        existingComment.setId(commentId);

        User user = new User();
        user.setId(userId);
        existingComment.setUser(user);

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(existingComment));
        mockedUserContext.when(UserContext::getUser).thenReturn(new UserDto(user));

        Exception exception = assertThrows(RuntimeException.class, () ->
                commentService.editComment(commentRequest, commentId)
        );

        assertEquals("Cannot update this field", exception.getMessage());

        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void editComment_ValidRequest_ShouldReturnUpdatedCommentResponse() {
        int userId = 1;
        int commentId = 10;
        String updatedContent = "Updated comment content";

        Comment existingComment = getComment(userId, commentId);

        CommentRequest request = new CommentRequest();
        request.setContent(updatedContent);

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(existingComment));
        when(commentRepository.save(any(Comment.class))).thenReturn(existingComment);

        CommentResponse response = commentService.editComment(request, commentId);

        assertNotNull(response);
        assertEquals(commentId, response.getId());
        assertEquals(userId, response.getUserId());
        assertEquals(updatedContent, response.getContent());

        verify(commentRepository).findById(commentId);
        verify(commentRepository).save(any(Comment.class));
    }

    private static Comment getComment(int userId, int commentId) {
        User user = new User();
        user.setId(userId);
        UserProfile userProfile = new UserProfile();
        userProfile.setFullName("John Doe");
        userProfile.setAvatarUrl("avatar.jpg");
        user.setUserProfile(userProfile);

        Post post = new Post();
        post.setId(2);

        Comment existingComment = new Comment();
        existingComment.setId(commentId);
        existingComment.setPost(post);
        existingComment.setUser(user);
        existingComment.setContent("Old content");
        return existingComment;
    }

    @Test
    void editComment_CommentNotOwnedByUser_ShouldThrowException() {
        int commentId = 10;

        User anotherUser = new User();
        anotherUser.setId(2);

        Comment existingComment = new Comment();
        existingComment.setId(commentId);
        existingComment.setUser(anotherUser);

        CommentRequest request = new CommentRequest();
        request.setContent("Updated content");

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(existingComment));

        assertThrows(UserForbiddenException.class, () -> commentService.editComment(request, commentId));

        verify(commentRepository).findById(commentId);
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void getCommentsByPostId_PostExists_PageGreaterThanZero_ShouldReturnComments() {
        int postId = 1;
        int page = 0; // Ensuring greater than 0
        int size = 10;

        Post post = new Post();
        post.setId(postId);

        User user = new User();
        user.setId(1);
        UserProfile userProfile = new UserProfile();
        userProfile.setFullName("John Doe");
        userProfile.setAvatarUrl("avatar.jpg");
        user.setUserProfile(userProfile);

        Comment comment = new Comment();
        comment.setId(1);
        comment.setPost(post);
        comment.setUser(user);
        comment.setContent("Test comment");

        Page<Comment> commentPage = new PageImpl<>(List.of(comment));

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(commentRepository.findAllByPostId(eq(postId), any(Pageable.class))).thenReturn(commentPage);

        List<CommentResponse> responses = commentService.getCommentsByPostId(postId, page, size);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(1, responses.getFirst().getId());
        assertEquals(1, responses.getFirst().getUserId());
        assertEquals("John Doe", responses.getFirst().getUserFullName());
        assertEquals("avatar.jpg", responses.getFirst().getUserProfileImage());

        verify(postRepository).findById(postId);
        verify(commentRepository).findAllByPostId(eq(postId), any(Pageable.class));
    }

    @Test
    void getCommentsByPostId_PostExists_ShouldReturnComments() {
        int postId = 1;
        int page = 1;
        int size = 10;

        Post post = new Post();
        post.setId(postId);

        User user = new User();
        user.setId(2);
        UserProfile userProfile = new UserProfile();
        userProfile.setFullName("John Doe");
        userProfile.setAvatarUrl("avatar.jpg");
        user.setUserProfile(userProfile);

        Comment comment = new Comment();
        comment.setId(1);
        comment.setPost(post);
        comment.setUser(user);
        comment.setContent("Test comment");

        Page<Comment> commentPage = new PageImpl<>(List.of(comment));

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(commentRepository.findAllByPostId(eq(postId), any(Pageable.class))).thenReturn(commentPage);

        List<CommentResponse> responses = commentService.getCommentsByPostId(postId, page, size);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(1, responses.getFirst().getId());
        assertEquals(2, responses.getFirst().getUserId());
        assertEquals("John Doe", responses.getFirst().getUserFullName());
        assertEquals("avatar.jpg", responses.getFirst().getUserProfileImage());

        verify(postRepository).findById(postId);
        verify(commentRepository).findAllByPostId(eq(postId), any(Pageable.class));
    }

    @Test
    void getCommentsByPostId_PostNotFound_ShouldThrowException() {
        int postId = 1;
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> commentService.getCommentsByPostId(postId, 0, 10));

        verify(postRepository).findById(postId);
        verifyNoInteractions(commentRepository);
    }

    @Test
    void getCommentById_CommentExists_ShouldReturnCommentResponse() {
        int commentId = 1;
        int postId = 2;
        int userId = 3;

        Post post = new Post();
        post.setId(postId);

        User user = new User();
        user.setId(userId);
        UserProfile userProfile = new UserProfile();
        userProfile.setFullName("Jane Doe");
        userProfile.setAvatarUrl("profile.jpg");
        user.setUserProfile(userProfile);

        Comment comment = new Comment();
        comment.setId(commentId);
        comment.setPost(post);
        comment.setUser(user);
        comment.setContent("This is a test comment.");
        comment.setCreatedAt(LocalDateTime.now());

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        CommentResponse response = commentService.getCommentById(commentId);

        assertNotNull(response);
        assertEquals(commentId, response.getId());
        assertEquals(postId, response.getPostId());
        assertEquals(userId, response.getUserId());
        assertEquals("Jane Doe", response.getUserFullName());
        assertEquals("profile.jpg", response.getUserProfileImage());
        assertEquals("This is a test comment.", response.getContent());

        verify(commentRepository).findById(commentId);
    }

    @Test
    void getCommentById_CommentNotFound_ShouldThrowException() {
        int commentId = 1;
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> commentService.getCommentById(commentId));

        verify(commentRepository).findById(commentId);
    }

    @Test
    void deleteComment_CommentExistsAndOwnedByUser_ShouldDeleteSuccessfully() {
        when(commentRepository.findById(10)).thenReturn(Optional.of(comment));

        commentService.deleteComment(10);

        verify(commentRepository).delete(comment);
    }

    @Test
    void deleteComment_CommentNotFound_ShouldThrowEntityNotFoundException() {
        when(commentRepository.findById(99)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class, () -> commentService.deleteComment(99));

        assertEquals("Comment not found for id: 99", exception.getMessage());
        verify(commentRepository, never()).delete((Comment) any());
    }

    @Test
    void deleteComment_CommentExistsButNotOwnedByUser_ShouldThrowUserForbiddenException() {
        User anotherUser = new User();
        anotherUser.setId(2);
        comment.setUser(anotherUser); // Comment belongs to another user

        when(commentRepository.findById(10)).thenReturn(Optional.of(comment));

        Exception exception = assertThrows(UserForbiddenException.class, () -> commentService.deleteComment(10));

        assertEquals("You are not allowed to delete this comment", exception.getMessage());
        verify(commentRepository, never()).delete((Comment) any());
    }
}
