package com.example.pm_tool.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class SprintRequest {
    @NotBlank
    private String name;
    private String goal;
    private LocalDate startDate;
    private LocalDate endDate;
}
