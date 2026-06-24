package com.codereviewer.controller;

import com.codereviewer.dto.ReviewRequest;
import com.codereviewer.dto.ReviewResponse;
import com.codereviewer.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Slf4j
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ReviewResponse> submitReview(@Valid @RequestBody ReviewRequest request) {
        log.info("Received review request for file: {} ({} characters)",
                request.getFilename(), request.getCode().length());
        ReviewResponse response = reviewService.reviewCode(request);
        log.info("Review complete: {} findings", response.getTotalFindings());
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ReviewResponse>> getRecentReviews() {
        List<ReviewResponse> reviews = reviewService.getRecentReviews();
        return ResponseEntity.ok(reviews);
    }
}