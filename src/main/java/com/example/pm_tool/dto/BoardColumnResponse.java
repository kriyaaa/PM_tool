package com.example.pm_tool.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class BoardColumnResponse {
    private String status;
    private String category;
    private Integer order;
    private List<IssueResponse> issues;
}
