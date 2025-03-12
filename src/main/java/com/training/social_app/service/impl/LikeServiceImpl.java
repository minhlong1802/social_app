package com.training.social_app.service.impl;

import com.training.social_app.entity.Like;
import com.training.social_app.repository.LikeRepository;
import com.training.social_app.service.LikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Calendar;

@Service
@RequiredArgsConstructor
@Slf4j
public class LikeServiceImpl implements LikeService {
    private final LikeRepository likeRepository;

    private Integer getCurrentUserId() {
        return 1;
    }
    @Override
    public Like likePost(Integer postId) {
        Integer userId = getCurrentUserId();
        //Check if the user has already liked the post
        Like existingLike = likeRepository.findByUserIdAndPostId(userId, postId).orElseThrow(() -> new RuntimeException("User has already liked the post"));
//        //Handle validation
//        Post post = postRepository.findById(likeRequestDto.getPostId()).orElseThrow(() -> new RuntimeException("Post not found for id: " + likeRequestDto.getPostId()));
//        User user = userRepository.findById(likeRequestDto.getUserId()).orElseThrow(() -> new RuntimeException("User not found for id: " + likeRequestDto.getUserId()));

        //Create a new like
        Like like = new Like();
//        like.setPost(post);
//        like.setUser(user);
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
        LocalDate endDate = LocalDate.now();
        return likeRepository.countLikesByUserAndDate(userId, startDate, endDate);
    }
}
