package com.codereviewer.controller;

import com.codereviewer.dto.ReviewRequest;
import com.codereviewer.dto.ReviewResponse;
import com.codereviewer.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ReviewResponse> submitReview(@RequestBody ReviewRequest request) {
        ReviewResponse response = reviewService.reviewCode(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ReviewResponse>> getRecentReviews() {
        List<ReviewResponse> reviews = reviewService.getRecentReviews();
        return ResponseEntity.ok(reviews);
    }
}
