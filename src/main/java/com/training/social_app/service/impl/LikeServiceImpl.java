package com.training.social_app.service.impl;

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
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;

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

    @Override
    public Like likePost(Integer postId) {
        Integer userId = getCurrentUserId();
        //Check if the user has already liked the post
        Like existingLike = likeRepository.findByUserIdAndPostId(userId, postId).orElseThrow(() -> new RuntimeException("User has already liked the post"));
        //Handle validation
        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found for id: " + postId));
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found for id: " + userId));

        //Create a new like
        Like like = new Like();
        like.setPost(post);
        like.setUser(user);
        return likeRepository.save(like);
    }

    @Override
    public void unlikePost(Integer postId) {
        Integer userId = getCurrentUserId();
        //Check if the user has already liked the post
        Like existingLike = likeRepository.findByUserIdAndPostId(userId, postId).orElseThrow(() -> new RuntimeException("User has not liked the post"));
        likeRepository.delete(existingLike);
    }

    @Override
    public int countLikesForUserInPastWeek() {
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
        return likeRepository.countLikesByUserAndDate(userId, startDateTime, endDateTime);
    }
}
