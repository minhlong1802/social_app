package com.training.social_app.Like.service;

import com.training.social_app.dto.response.LikeResponse;
import com.training.social_app.entity.Like;
import com.training.social_app.entity.Post;
import com.training.social_app.entity.User;
import com.training.social_app.entity.UserProfile;
import com.training.social_app.repository.LikeRepository;
import com.training.social_app.repository.PostRepository;
import com.training.social_app.repository.UserRepository;
import com.training.social_app.service.impl.LikeServiceImpl;
import com.training.social_app.utils.UserContext;
import com.training.social_app.dto.response.UserDto;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LikeServiceImplTest {

    @InjectMocks
    private LikeServiceImpl likeService;

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostRepository postRepository;

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
    }

    @Test
    void likePost_PostNotFound_ShouldThrowException() {
        // Mock user retrieval to ensure getCurrentUserId() succeeds
        User user = new User();
        user.setId(1);
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));

        // Mock post retrieval failure
        when(postRepository.findById(anyInt())).thenReturn(Optional.empty());

        // Expect EntityNotFoundException when calling likePost()
        assertThrows(EntityNotFoundException.class, () -> likeService.likePost(1));

        // Verify userRepository is called first
        verify(userRepository).findById(anyInt());

        // Verify postRepository is called once
        verify(postRepository).findById(anyInt());
    }

    @Test
    void likePost_UserNotFound_ShouldThrowException() {
        // Mock userRepository to return empty user (triggers EntityNotFoundException)
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        // Expect EntityNotFoundException when calling likePost()
        assertThrows(EntityNotFoundException.class, () -> likeService.likePost(1));

        // Verify userRepository is called once
        verify(userRepository).findById(anyInt());

        // Ensure postRepository is never called since the user is not found
        verify(postRepository, never()).findById(anyInt());
    }

    @Test
    void likePost_ExistingLike_ShouldUnlikePost() {
        // Create Post
        Post post = new Post();
        post.setId(1);

        // Create UserProfile and set properties
        UserProfile userProfile = new UserProfile();
        userProfile.setFullName("John Doe");
        userProfile.setAvatarUrl("avatar.jpg");

        // Create User and associate UserProfile
        User user = new User();
        user.setId(1);
        user.setUserProfile(userProfile); // Ensure UserProfile is not null

        // Create Like object
        Like like = new Like();
        like.setPost(post);
        like.setUser(user);

        // Mock repository responses (allow multiple calls)
        when(postRepository.findById(anyInt())).thenReturn(Optional.of(post));
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user)); // Allow multiple calls
        when(likeRepository.findByUserIdAndPostId(anyInt(), anyInt())).thenReturn(Optional.of(like));

        // Call service method
        LikeResponse response = likeService.likePost(1);

        // Assertions
        assertNotNull(response);
        assertEquals(1, response.getPostId());
        assertEquals(1, response.getUserId());
        assertEquals("John Doe", response.getUserFullName());
        assertEquals("avatar.jpg", response.getUserProfileImage());

        // Verify interactions (allow 2 calls to userRepository.findById)
        verify(postRepository).findById(anyInt());
        verify(userRepository, times(2)).findById(anyInt()); // Expecting 2 invocations
        verify(likeRepository).findByUserIdAndPostId(anyInt(), anyInt());
        verify(likeRepository).delete(any(Like.class)); // Ensure the like is deleted
    }


    @Test
    void likePost_NewLike_ShouldSaveLike() {
        // Create Post
        Post post = new Post();
        post.setId(1);

        // Create UserProfile and set properties
        UserProfile userProfile = new UserProfile();
        userProfile.setFullName("John Doe");
        userProfile.setAvatarUrl("avatar.jpg");

        // Create User and associate UserProfile
        User user = new User();
        user.setId(1);
        user.setUserProfile(userProfile); // Ensure UserProfile is not null

        // Create Like object
        Like like = new Like();
        like.setPost(post);
        like.setUser(user);

        // Mock repository responses (allow multiple calls)
        when(postRepository.findById(anyInt())).thenReturn(Optional.of(post));
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user)); // Allow multiple calls
        when(likeRepository.findByUserIdAndPostId(anyInt(), anyInt())).thenReturn(Optional.empty());
        when(likeRepository.save(any(Like.class))).thenReturn(like);

        // Call service method
        LikeResponse response = likeService.likePost(1);

        // Assertions
        assertNotNull(response);
        assertEquals(1, response.getPostId());
        assertEquals(1, response.getUserId());
        assertEquals("John Doe", response.getUserFullName());
        assertEquals("avatar.jpg", response.getUserProfileImage());

        // Verify interactions (allow 2 calls to userRepository.findById)
        verify(postRepository).findById(anyInt());
        verify(userRepository, times(2)).findById(anyInt()); // Expecting 2 invocations
        verify(likeRepository).findByUserIdAndPostId(anyInt(), anyInt());
        verify(likeRepository).save(any(Like.class));
    }

    @Test
    void getLikesForPost_PostExists_ShouldReturnLikes() {
        int postId = 1;
        int page = 1;
        int size = 10;
        int userId = 2;

        // Create Post
        Post post = new Post();
        post.setId(postId);

        // Create User
        User user = new User();
        user.setId(userId);
        UserProfile userProfile = new UserProfile();
        userProfile.setFullName("John Doe");
        userProfile.setAvatarUrl("avatar.jpg");
        user.setUserProfile(userProfile);

        // Create Like
        Like like = new Like();
        like.setId(1);
        like.setPost(post);
        like.setUser(user); // Set the user to prevent NPE

        Page<Like> likePage = new PageImpl<>(List.of(like));

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(likeRepository.findByPostId(eq(postId), any(Pageable.class))).thenReturn(likePage);

        List<LikeResponse> responses = likeService.getLikesForPost(postId, page, size);

        // Assertions
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(postId, responses.getFirst().getPostId());
        assertEquals(userId, responses.getFirst().getUserId());
        assertEquals("John Doe", responses.getFirst().getUserFullName());
        assertEquals("avatar.jpg", responses.getFirst().getUserProfileImage());

        verify(postRepository).findById(postId);
        verify(likeRepository).findByPostId(eq(postId), any(Pageable.class));
    }

    @Test
    void getLikesForPost_PageZero_ShouldNotDecrementPage() {
        int postId = 1;
        int page = 0;  // Page = 0 to test the uncovered condition
        int size = 10;
        int userId = 2;

        Post post = new Post();
        post.setId(postId);

        User user = new User();
        user.setId(userId);
        UserProfile userProfile = new UserProfile();
        userProfile.setFullName("John Doe");
        userProfile.setAvatarUrl("avatar.jpg");
        user.setUserProfile(userProfile);

        Like like = new Like();
        like.setId(1);
        like.setPost(post);
        like.setUser(user);

        Page<Like> likePage = new PageImpl<>(List.of(like));

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(likeRepository.findByPostId(eq(postId), any(Pageable.class))).thenReturn(likePage);

        List<LikeResponse> responses = likeService.getLikesForPost(postId, page, size);

        assertNotNull(responses);
        assertEquals(1, responses.size());

        verify(postRepository).findById(postId);
        verify(likeRepository).findByPostId(eq(postId), any(Pageable.class));
    }

    @Test
    void getLikesForPost_DatabaseError_ShouldReturnNull() {
        int postId = 1;
        int page = 1;
        int size = 10;

        when(postRepository.findById(postId)).thenReturn(Optional.of(new Post()));
        when(likeRepository.findByPostId(eq(postId), any(Pageable.class)))
                .thenThrow(new RuntimeException("Database error"));  // Force exception

        List<LikeResponse> responses = likeService.getLikesForPost(postId, page, size);

        assertNull(responses);  // Expect null due to exception handling

        verify(postRepository).findById(postId);
        verify(likeRepository).findByPostId(eq(postId), any(Pageable.class));
    }

    @Test
    void getLikesForPost_PostNotFound_ShouldThrowException() {
        int postId = 1;
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> likeService.getLikesForPost(postId, 1, 10));

        verify(postRepository).findById(postId);
        verifyNoInteractions(likeRepository);
    }

    @Test
    void getLikeById_LikeExists_ShouldReturnLikeResponse() {
        int likeId = 1;
        int postId = 2;
        int userId = 3;

        // Create Post
        Post post = new Post();
        post.setId(postId);

        // Create User
        User user = new User();
        user.setId(userId);
        UserProfile userProfile = new UserProfile();
        userProfile.setFullName("John Doe");
        userProfile.setAvatarUrl("avatar.jpg");
        user.setUserProfile(userProfile);

        // Create Like
        Like like = new Like();
        like.setId(likeId);
        like.setPost(post);
        like.setUser(user);

        when(likeRepository.findById(likeId)).thenReturn(Optional.of(like));

        LikeResponse response = likeService.getLikeById(likeId);

        assertNotNull(response);
        assertEquals(likeId, response.getId());
        assertEquals(postId, response.getPostId());
        assertEquals(userId, response.getUserId());
        assertEquals("John Doe", response.getUserFullName());
        assertEquals("avatar.jpg", response.getUserProfileImage());

        verify(likeRepository).findById(likeId);
    }

    @Test
    void getLikeById_LikeNotFound_ShouldThrowException() {
        int likeId = 1;
        when(likeRepository.findById(likeId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> likeService.getLikeById(likeId));

        verify(likeRepository).findById(likeId);
    }
}
