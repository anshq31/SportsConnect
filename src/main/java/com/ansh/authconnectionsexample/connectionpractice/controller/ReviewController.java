package com.ansh.authconnectionsexample.connectionpractice.controller;

import com.ansh.authconnectionsexample.connectionpractice.dto.ReviewDto;
import com.ansh.authconnectionsexample.connectionpractice.dto.ReviewRequest;
import com.ansh.authconnectionsexample.connectionpractice.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
