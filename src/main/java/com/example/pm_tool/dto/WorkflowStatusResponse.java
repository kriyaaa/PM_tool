package com.example.pm_tool.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WorkflowStatusResponse {
    private String id;
    private String name;
    private String category;
    private Integer order;
}
