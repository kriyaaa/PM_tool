package com.example.pm_tool.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class SprintResponse {
    private String id;
    private String projectId;
    private String name;
    private String goal;
    private String state;
    private LocalDate startDate;
    private LocalDate endDate;
}
