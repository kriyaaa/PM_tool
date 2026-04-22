package com.example.pm_tool.dto;

import com.example.pm_tool.entity.enums.Priority;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
public class IssueResponse {
    private String id;
    private String issueKey;
    private String projectId;
    private String type;
    private String title;
    private String description;
    private String status;
    private Priority priority;
    private Integer storyPoints;
    private String assigneeId;
    private String assigneeName;
    private String reporterId;
    private String reporterName;
    private String reviewerId;
    private String reviewerName;
    private String sprintId;
    private String sprintName;
    private String parentId;
    private Set<String> labels;
    private Set<String> watcherIds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
