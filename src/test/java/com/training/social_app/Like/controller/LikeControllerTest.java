package com.training.social_app.Like.controller;

import com.training.social_app.controller.LikeController;
import com.training.social_app.dto.response.LikeResponse;
import com.training.social_app.service.LikeService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LikeControllerTest {

    @Mock
    private LikeService likeService;

    @InjectMocks
    private LikeController likeController;

    private LikeResponse mockLikeResponse;

    @BeforeEach
    void setUp() {
        mockLikeResponse = new LikeResponse(); // Assume LikeResponse has proper fields
    }

    @Test
    void likePost_ValidPostId_ShouldReturnSuccessResponse() {
        when(likeService.likePost(1)).thenReturn(mockLikeResponse);

        ResponseEntity<Object> response = likeController.likePost("1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(likeService, times(1)).likePost(1);
    }

    @Test
    void likePost_InvalidPostId_ShouldReturnBadRequest() {
        ResponseEntity<Object> response = likeController.likePost("abc");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void likePost_PostNotFound_ShouldReturnNotFound() {
        when(likeService.likePost(1)).thenThrow(new EntityNotFoundException("Post not found"));

        ResponseEntity<Object> response = likeController.likePost("1");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(likeService, times(1)).likePost(1);
    }

    @Test
    void likePost_PostIdLessThanOrEqualZero_ShouldReturnBadRequest() {
        ResponseEntity<Object> response = likeController.likePost("0");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).toString().contains("Post id must be greater than 0"));

        response = likeController.likePost("-1");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).toString().contains("Post id must be greater than 0"));
    }

    @Test
    void likePost_UnexpectedError_ShouldReturnInternalServerError() {
        when(likeService.likePost(1)).thenThrow(new RuntimeException("Database error"));

        ResponseEntity<Object> response = likeController.likePost("1");

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).toString().contains("An unexpected error occurred"));
        verify(likeService, times(1)).likePost(1);
    }

    @Test
    void getLikesForPost_InvalidPostId_ShouldReturnBadRequest() {
        ResponseEntity<Object> response = likeController.getLikesForPost("abc", 1, 10);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void getLikesForPost_PostIdLessThanOrEqualZero_ShouldReturnBadRequest() {
        ResponseEntity<Object> response = likeController.getLikesForPost("0",1, 10);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).toString().contains("Post id must be greater than 0"));

        response = likeController.getLikesForPost("-1",1,10);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).toString().contains("Post id must be greater than 0"));
    }

    @Test
    void getLikesForPost_ValidRequest_ShouldReturnSuccess() {
        Map<String, Object> mockLikeMap = Map.of(
                "listLike", List.of(mockLikeResponse),
                "pageSize", 10,
                "pageNo", 1,
                "totalPage", 1
        );
        when(likeService.getLikesForPost(1, 1, 10)).thenReturn(mockLikeMap);

        ResponseEntity<Object> response = likeController.getLikesForPost("1", 1, 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(likeService, times(1)).getLikesForPost(1, 1, 10);
    }

    @Test
    void getLikesForPost_PostNotFound_ShouldReturnNotFound() {
        when(likeService.getLikesForPost(1, 1, 10)).thenThrow(new EntityNotFoundException("Post not found"));

        ResponseEntity<Object> response = likeController.getLikesForPost("1", 1, 10);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertFalse(Objects.requireNonNull(response.getBody()).toString().contains("Post not found"));
        verify(likeService, times(1)).getLikesForPost(1, 1, 10);
    }

    @Test
    void getLikesForPost_UnexpectedError_ShouldReturnInternalServerError() {
        when(likeService.getLikesForPost(1, 1, 10)).thenThrow(new RuntimeException("Database error"));

        ResponseEntity<Object> response = likeController.getLikesForPost("1", 1, 10);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).toString().contains("An unexpected error occurred"));
        verify(likeService, times(1)).getLikesForPost(1, 1, 10);
    }

    @Test
    void getLikeById_ValidLikeId_ShouldReturnSuccess() {
        when(likeService.getLikeById(1)).thenReturn(mockLikeResponse);

        ResponseEntity<Object> response = likeController.getLikeById("1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(likeService, times(1)).getLikeById(1);
    }

    @Test
    void getLikeById_InvalidLikeId_ShouldReturnBadRequest() {
        ResponseEntity<Object> response = likeController.getLikeById("abc");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void getLikeById_PostIdLessThanOrEqualZero_ShouldReturnBadRequest() {
        ResponseEntity<Object> response = likeController.getLikeById("0");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).toString().contains("Like id must be greater than 0"));

        response = likeController.getLikeById("-1");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).toString().contains("Like id must be greater than 0"));
    }

    @Test
    void getLikeById_LikeNotFound_ShouldReturnNotFound() {
        when(likeService.getLikeById(1)).thenThrow(new EntityNotFoundException("Like not found"));

        ResponseEntity<Object> response = likeController.getLikeById("1");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(likeService, times(1)).getLikeById(1);
    }

    @Test
    void getLikeById_UnexpectedError_ShouldReturnInternalServerError() {
        when(likeService.getLikeById(1)).thenThrow(new RuntimeException("Database error"));

        ResponseEntity<Object> response = likeController.getLikeById("1");

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).toString().contains("An unexpected error occurred"));
        verify(likeService, times(1)).getLikeById(1);
    }
}
