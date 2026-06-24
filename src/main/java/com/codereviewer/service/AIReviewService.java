package com.codereviewer.service;

import com.codereviewer.dto.Finding;
import com.codereviewer.dto.ReviewRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AIReviewService {

    private final ChatClient.Builder chatClientBuilder;
    private final ObjectMapper objectMapper;

    private static final String SYSTEM_PROMPT = """
            You are a paranoid senior software engineer performing an adversarial code review.
            Your ONLY job is to find ways this code will FAIL in production.
            
            Focus exclusively on these categories:
            - CONCURRENCY: Race conditions, thread safety, deadlocks
            - NULL_SAFETY: Null pointer dereferences, missing null checks
            - ERROR_HANDLING: Swallowed exceptions, missing error cases, poor recovery
            - SECURITY: Injection, auth bypass, data exposure, input validation
            - RESOURCE_LEAK: Unclosed connections/streams, memory leaks, unbounded growth
            - PERFORMANCE: N+1 queries, unbounded collections, blocking in async
            - EDGE_CASE: Off-by-one, boundary conditions, integer overflow, empty collections
            - EXTERNAL_DEPENDENCY: Missing timeouts, no retries, no circuit breakers
            
            DO NOT comment on:
            - Code style, naming conventions, formatting
            - Missing documentation or comments
            - Design patterns or architecture preferences
            
            For each issue found, respond in this EXACT JSON format.
            No markdown, no backticks, no explanation — ONLY the raw JSON array:
            [
              {
                "category": "ONE_OF_THE_CATEGORIES_ABOVE",
                "severity": "CRITICAL or WARNING or SUGGESTION",
                "description": "What the problem is",
                "failureScenario": "The specific production scenario where this breaks",
                "suggestion": "Concrete fix with code if possible",
                "affectedLines": "Line numbers or N/A if unclear"
              }
            ]
            
            If the code has NO issues, return an empty array: []
            
            Be thorough but precise. Only report real issues, not hypothetical ones.
            Every finding must include a concrete failure scenario.
            """;

    public List<Finding> analyzeCode(ReviewRequest request) {
        log.info("Starting AI review for file: {}", request.getFilename());

        ChatClient chatClient = chatClientBuilder.build();

        String userMessage = String.format(
                "Review this %s code from file '%s':\n\n```\n%s\n```",
                request.getLanguage() != null ? request.getLanguage() : "unknown",
                request.getFilename() != null ? request.getFilename() : "unknown",
                request.getCode()
        );

        String response = chatClient.prompt()
                .system(SYSTEM_PROMPT)
                .user(userMessage)
                .call()
                .content();

        log.info("AI response received, parsing findings...");
        log.debug("Raw AI response: {}", response);

        return parseFindings(response);
    }

    private List<Finding> parseFindings(String response) {
        try {
            String cleaned = response.trim();
            if (cleaned.startsWith("```")) {
                cleaned = cleaned.replaceAll("^```[a-z]*\\n?", "")
                                 .replaceAll("```$", "")
                                 .trim();
            }

            return objectMapper.readValue(
                    cleaned,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Finding.class)
            );
        } catch (Exception e) {
            log.error("Failed to parse AI response: {}", response, e);
            return List.of(Finding.builder()
                    .category("PARSE_ERROR")
                    .severity("WARNING")
                    .description("AI response could not be parsed: " + e.getMessage())
                    .failureScenario("N/A")
                    .suggestion("Try again or review manually")
                    .affectedLines("N/A")
                    .build());
        }
    }
}