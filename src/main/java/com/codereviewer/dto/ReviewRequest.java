package com.codereviewer.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReviewRequest {
    private String code;
    private String language;
    private String filename;
}