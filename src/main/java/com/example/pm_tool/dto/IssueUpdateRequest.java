package com.example.pm_tool.dto;

import lombok.Data;

import java.util.Set;

@Data
public class IssueUpdateRequest {
    private String title;
    private String description;
    private String priority;
    private Integer storyPoints;
    private String assigneeId;
    private String reviewerId;
    private String sprintId;
    private Set<String> labels;
}
