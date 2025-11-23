package com.ansh.authconnectionsexample.connectionpractice.service;

import com.ansh.authconnectionsexample.connectionpractice.dto.ReviewDto;
import com.ansh.authconnectionsexample.connectionpractice.dto.UserProfileDto;
import com.ansh.authconnectionsexample.connectionpractice.dto.UserUpdateDto;
import com.ansh.authconnectionsexample.connectionpractice.model.gigAndReviewEnitities.Review;
import com.ansh.authconnectionsexample.connectionpractice.model.userAndAuthEntities.Skill;
import com.ansh.authconnectionsexample.connectionpractice.model.userAndAuthEntities.User;
import com.ansh.authconnectionsexample.connectionpractice.repository.ReviewRepository;
import com.ansh.authconnectionsexample.connectionpractice.repository.SkillRepository;
import com.ansh.authconnectionsexample.connectionpractice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SkillRepository skillRepository;
    @Autowired
    private ReviewRepository reviewRepository;

    private User getAuthenticatedUser(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(()-> new RuntimeException("Authenticated user not found"));
    }

    @Transactional(readOnly = true)
    public UserProfileDto getMyProfile(){
        System.out.println(getAuthenticatedUser());
        return mapToUserProfileDto(getAuthenticatedUser());
    }

    public UserProfileDto updateMyProfile(UserUpdateDto updateDto){
        User user = getAuthenticatedUser();

        user.setExperience(updateDto.getExperience());

        Set<Skill> skills = updateDto.getSkillIds().stream()
                .map(id-> skillRepository.findById(id)
                        .orElseThrow(()-> new RuntimeException("Skill not found with id"+ id)))
                .collect(Collectors.toSet());
        user.setSkills(skills);

        User updatedUser = userRepository.save(user);
        return mapToUserProfileDto(updatedUser);
    }

    @Transactional(readOnly = true)
    public UserProfileDto getUserPublicProfile(Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new RuntimeException("User not found with id"+ userId));
        return mapToUserProfileDto(user);
    }

    public void updateUserRating(Long userId){
        User  user = userRepository.findById(userId)
                .orElseThrow(()-> new RuntimeException("User not found with this id"+ userId));
        Double averageRating = reviewRepository.calculateAverageRating(userId);
        if (averageRating == null) {
            user.setOverallRating(BigDecimal.ZERO);
        }else{
            user.setOverallRating(BigDecimal.valueOf(averageRating));
        }

        userRepository.save(user);
    }


//    MAPPERS------
   private UserProfileDto mapToUserProfileDto(User user){
       return UserProfileDto.builder()
               .id(user.getId())
               .username(user.getUsername())
               .experience(user.getExperience())
               .overallRating(user.getOverallRating())
               .skill(user.getSkills().stream().map(Skill::getName).collect(Collectors.toSet()))
//               .reviewsReceived(user.getReviewsReceived().stream().map(this::mapToReviewDto).collect(Collectors.toList()))
               .build();
   }

   private ReviewDto mapToReviewDto(Review review){
        return ReviewDto.builder()
               .id(review.getId())
               .gigId(review.getGig().getId())
               .reviewerUsername(review.getReviewer().getUsername())
               .rating(review.getRating())
               .comment(review.getComment())
               .build();
   }

}
