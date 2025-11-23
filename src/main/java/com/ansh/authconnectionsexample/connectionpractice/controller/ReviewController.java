package com.ansh.authconnectionsexample.connectionpractice.controller;

import com.ansh.authconnectionsexample.connectionpractice.dto.ReviewDto;
import com.ansh.authconnectionsexample.connectionpractice.dto.ReviewRequest;
import com.ansh.authconnectionsexample.connectionpractice.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ReviewDto> submitReview(@Valid @RequestBody ReviewRequest request){
        ReviewDto savedReview = reviewService.createReview(request);
        return  new ResponseEntity<>(savedReview, HttpStatus.CREATED);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<ReviewDto>> getReviewsForUser(@PageableDefault(size = 5,sort = "id", direction = Sort.Direction.DESC) Pageable pageable, @PathVariable Long userId){
        Page<ReviewDto> reviews = reviewService.getReviewsForUser(userId,pageable);
        return ResponseEntity.ok(reviews);
    }
}
