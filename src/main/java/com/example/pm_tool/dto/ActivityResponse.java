package com.example.pm_tool.dto;

import com.example.pm_tool.entity.enums.ActivityType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ActivityResponse {
    private String id;
    private ActivityType type;
    private String details;
    private String actorId;
    private String actorName;
    private String issueId;
    private String issueKey;
    private LocalDateTime createdAt;
}
