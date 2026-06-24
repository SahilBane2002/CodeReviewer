package com.codereviewer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReviewRequest {

    @NotBlank(message = "Code cannot be empty")
    @Size(max = 50000, message = "Code is too long (max 50000 characters)")
    private String code;

    @Size(max = 50, message = "Language name too long")
    private String language;

    @Size(max = 255, message = "Filename too long")
    private String filename;
}