package com.ansh.sportsconnect.service;

import com.ansh.sportsconnect.dto.ReviewDto;
import com.ansh.sportsconnect.dto.ReviewRequest;
import com.ansh.sportsconnect.model.enums.GigStatus;
import com.ansh.sportsconnect.model.gigAndReviewEnitities.Gig;
import com.ansh.sportsconnect.model.gigAndReviewEnitities.Review;
import com.ansh.sportsconnect.model.userAndAuthEntities.User;
import com.ansh.sportsconnect.repository.GigRepository;
import com.ansh.sportsconnect.repository.ReviewRepository;
import com.ansh.sportsconnect.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private GigRepository gigRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    private User getAuthenticatedUser(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        return  userRepository.findByUsername(username).orElseThrow(()-> new UsernameNotFoundException("Authenticated User not found"));
    }

    public ReviewDto createReview(ReviewRequest reviewRequest){
        User gigMaster = getAuthenticatedUser();

        Gig gig = gigRepository.findById(reviewRequest.getGigId())
                .orElseThrow(()-> new RuntimeException("Gig not found"));

        User participant = userRepository.findById(reviewRequest.getParticipantId())
                .orElseThrow(()-> new RuntimeException("User not found"));

        if (!gig.getGigMaster().equals(gigMaster)){
            throw new RuntimeException("Only the gigi master can review participant");
        }

        if (gig.getStatus() != GigStatus.COMPLETED){
            throw  new RuntimeException("Reviews can only be given on completed gigs");
        }

        if (!gig.getAcceptedParticipants().contains(participant)){
            throw new RuntimeException("This user was not a participant in the gig");
        }

        if (reviewRepository.existsByGigIdAndParticipant(gig.getId(),participant)){
            throw new RuntimeException("You have already reviewed this participant");
        }

        Review review = Review.builder()
                .gigId(gig.getId())
                .reviewer(gigMaster)
                .participant(participant)
                .comment(reviewRequest.getComment())
                .rating(reviewRequest.getRating())
                .build();
        Review savedReview = reviewRepository.save(review);

        userService.updateUserRating(participant.getId());

        return mapToReviewDto(savedReview);

    }

    @Transactional(readOnly = true)
    public Page<ReviewDto> getReviewsForUser(Long participantId, Pageable pageable){
        User participant = userRepository.findById(participantId)
                .orElseThrow(()-> new UsernameNotFoundException("Cannot find participant with id"+participantId));

        Page<Review> reviewPage = reviewRepository.findByParticipant(participant,pageable);

        return reviewPage.map(this::mapToReviewDto);
    }

    public ReviewDto mapToReviewDto(Review review){
        return ReviewDto.builder()
                .id(review.getId())
                .reviewerUsername(review.getReviewer().getUsername())
                .gigId(review.getGigId())
                .comment(review.getComment())
                .rating(review.getRating())
                .build();
    }

}
