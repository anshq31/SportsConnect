package com.ansh.sportsconnect.service;

import com.ansh.sportsconnect.dto.ReviewDto;
import com.ansh.sportsconnect.dto.UserProfileDto;
import com.ansh.sportsconnect.dto.UserUpdateDto;
import com.ansh.sportsconnect.model.gigAndReviewEnitities.Review;
import com.ansh.sportsconnect.model.userAndAuthEntities.Skill;
import com.ansh.sportsconnect.model.userAndAuthEntities.User;
import com.ansh.sportsconnect.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
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
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    @Autowired
    private ChatMessageRepository chatMessageRepository;
    @Autowired
    private GigRequestRepository gigRequestRepository;
    @Autowired
    private GigRepository gigRepository;
    @Autowired
    private ChatGroupRepository chatGroupRepository;
    @Autowired
    private UserBlockRepository userBlockRepository;
    @Autowired
    private MessageReportRepository messageReportRepository;

    private User getAuthenticatedUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));
    }

    @Transactional(readOnly = true)
    public UserProfileDto getMyProfile() {
        User user = getAuthenticatedUser();
        List<Long> blockedUserIds = userBlockRepository.findBlockedIdsByBlockerId(user.getId());
        return mapToUserProfileDto(user, blockedUserIds);
    }

    public UserProfileDto updateMyProfile(UserUpdateDto updateDto) {
        User user = getAuthenticatedUser();

        user.setExperience(updateDto.getExperience());

        Set<Skill> skills = updateDto.getSkillIds().stream()
                .map(id -> skillRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Skill not found with id" + id)))
                .collect(Collectors.toSet());
        user.setSkills(skills);

        User updatedUser = userRepository.save(user);
        return mapToUserProfileDto(updatedUser);
    }

    @Transactional(readOnly = true)
    public UserProfileDto getUserPublicProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id" + userId));
        return mapToUserProfileDto(user);
    }

    public void updateUserRating(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with this id" + userId));
        Double averageRating = reviewRepository.calculateAverageRating(userId);
        if (averageRating == null) {
            user.setOverallRating(BigDecimal.ZERO);
        } else {
            user.setOverallRating(BigDecimal.valueOf(averageRating));
        }
        userRepository.save(user);
    }

    @Transactional
    public void deleteMyAccount() {
        User user = getAuthenticatedUser();
        Long userId = user.getId();

        // 1. Revoke refresh tokens
        refreshTokenRepository.deleteByUserId(userId);

        // 2. Delete all reviews the user gave or received
        reviewRepository.deleteAllInvolvingUser(userId);

        // 3. Delete message reports filed by this user
        messageReportRepository.deleteAllByReporterId(userId);

        // 4. Delete all chat messages sent by this user
        chatMessageRepository.deleteAllBySenderId(userId);

        // 5. Delete gig requests this user made as requester
        gigRequestRepository.deleteAllByRequesterId(userId);

        // 6. Handle gigs where this user is the gig master
        List<Long> ownedGigIds = gigRepository.findIdsByGigMasterId(userId);
        if (!ownedGigIds.isEmpty()) {
            // Delete join requests for owned gigs
            gigRequestRepository.deleteAllByGigIdIn(ownedGigIds);
            // Delete messages in owned gig chat groups (from other participants)
            chatMessageRepository.deleteAllByGigIds(ownedGigIds);
            // Delete chat group member entries for owned gigs
            chatGroupRepository.deleteMembersForGigIds(ownedGigIds);
            // Delete chat groups for owned gigs
            chatGroupRepository.deleteAllByGigIdIn(ownedGigIds);
            // Delete participant join table entries for owned gigs
            gigRepository.clearParticipantsByGigIds(ownedGigIds);
            // Delete the gigs themselves
            gigRepository.deleteAllByIdIn(ownedGigIds);
        }

        // 7. Remove user from participant lists in gigs they joined (but didn't own)
        gigRepository.removeUserFromAllParticipants(userId);

        // 8. Remove user from chat group member lists (groups from others' gigs)
        chatGroupRepository.removeUserFromAllGroups(userId);

        // 9. Delete all block records involving this user
        userBlockRepository.deleteAllInvolvingUser(userId);

        // 10. Delete the user (cascade removes user_skills join table entries)
        userRepository.deleteById(userId);
    }

//    MAPPERS------
    private UserProfileDto mapToUserProfileDto(User user) {
        return mapToUserProfileDto(user, null);
    }

    private UserProfileDto mapToUserProfileDto(User user, List<Long> blockedUserIds) {
        return UserProfileDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .experience(user.getExperience())
                .overallRating(user.getOverallRating())
                .skill(user.getSkills().stream().map(Skill::getName).collect(Collectors.toSet()))
                .blockedUserIds(blockedUserIds)
                .build();
    }

    private ReviewDto mapToReviewDto(Review review) {
        return ReviewDto.builder()
                .id(review.getId())
                .gigId(review.getGigId())
                .reviewerUsername(review.getReviewer().getUsername())
                .rating(review.getRating())
                .comment(review.getComment())
                .build();
    }
}
