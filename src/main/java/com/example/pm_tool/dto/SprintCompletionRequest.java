package com.example.pm_tool.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.LinkedHashSet;
import java.util.Set;

@Data
public class SprintCompletionRequest {
    @NotBlank
    private String actorId;
    private String nextSprintId;
    private Set<String> carryOverIssueIds = new LinkedHashSet<>();
}
