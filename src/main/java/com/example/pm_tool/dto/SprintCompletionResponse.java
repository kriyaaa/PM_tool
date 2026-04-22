package com.example.pm_tool.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SprintCompletionResponse {
    private SprintResponse sprint;
    private List<IssueResponse> incompleteIssues;
    private Integer completedStoryPoints;
}
