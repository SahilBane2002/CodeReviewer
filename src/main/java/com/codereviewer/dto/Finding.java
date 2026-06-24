package com.codereviewer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Finding {
    private String category;
    private String severity;
    private String description;
    private String failureScenario;
    private String suggestion;
    private String affectedLines;
}
