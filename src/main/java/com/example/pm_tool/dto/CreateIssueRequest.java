package com.example.pm_tool.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

@Data
public class CreateIssueRequest {

    @NotBlank
    private String type;

    @NotBlank
    private String title;

    private String description;

    @NotBlank
    private String priority;

    @NotNull
    private Integer storyPoints;

    @NotBlank
    private String reporterId;

    private String assigneeId;
    private String reviewerId;
    private String sprintId;
    private String parentId;
    private Set<String> labels = new LinkedHashSet<>();
    private Map<String, String> customFields;
}
