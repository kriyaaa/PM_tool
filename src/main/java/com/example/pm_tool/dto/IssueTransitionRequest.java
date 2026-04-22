package com.example.pm_tool.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class IssueTransitionRequest {

    @NotBlank
    private String userId;

    @NotBlank
    private String toStatus;
}
