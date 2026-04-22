package com.example.pm_tool.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class BoardResponse {
    private String projectId;
    private String projectKey;
    private String projectName;
    private List<BoardColumnResponse> columns;
}
