package com.example.pm_tool.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ProjectResponse {
    private String id;
    private String key;
    private String name;
    private String description;
    private String ownerId;
    private String ownerName;
    private List<WorkflowStatusResponse> statuses;
}
