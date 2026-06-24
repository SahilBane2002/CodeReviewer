package com.codereviewer.service;

import com.codereviewer.dto.Finding;
import com.codereviewer.dto.ReviewRequest;
import com.codereviewer.dto.ReviewResponse;
import com.codereviewer.entity.ReviewRecord;
import com.codereviewer.repository.ReviewRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ObjectMapper objectMapper;
    private final AIReviewService aiReviewService;

    public ReviewResponse reviewCode(ReviewRequest request) {
        List<Finding> findings = aiReviewService.analyzeCode(request);

        ReviewRecord record = saveReview(request, findings);

        return ReviewResponse.builder()
                .id(record.getId())
                .filename(request.getFilename())
                .language(request.getLanguage())
                .reviewedAt(record.getReviewedAt())
                .totalFindings(findings.size())
                .findings(findings)
                .build();
    }

    public List<ReviewResponse> getRecentReviews() {
        return reviewRepository.findTop10ByOrderByReviewedAtDesc().stream()
                .map(this::toResponse)
                .toList();
    }

    private ReviewRecord saveReview(ReviewRequest request, List<Finding> findings) {
        try {
            ReviewRecord record = ReviewRecord.builder()
                    .code(request.getCode())
                    .language(request.getLanguage())
                    .filename(request.getFilename())
                    .totalFindings(findings.size())
                    .findingsJson(objectMapper.writeValueAsString(findings))
                    .reviewedAt(Instant.now())
                    .build();
            return reviewRepository.save(record);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize findings", e);
        }
    }

    private ReviewResponse toResponse(ReviewRecord record) {
        try {
            List<Finding> findings = objectMapper.readValue(
                    record.getFindingsJson(), new TypeReference<>() {});
            return ReviewResponse.builder()
                    .id(record.getId())
                    .filename(record.getFilename())
                    .language(record.getLanguage())
                    .reviewedAt(record.getReviewedAt())
                    .totalFindings(record.getTotalFindings())
                    .findings(findings)
                    .build();
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to deserialize findings", e);
        }
    }
}