package com.example.pm_tool.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProjectRequest {
    @NotBlank
    private String name;
    @NotBlank
    private String key;
    private String description;
    @NotBlank
    private String ownerId;
}
