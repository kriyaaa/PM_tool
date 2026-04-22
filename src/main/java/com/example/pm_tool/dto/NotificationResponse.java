package com.example.pm_tool.dto;

import com.example.pm_tool.entity.enums.NotificationType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class NotificationResponse {
    private String id;
    private NotificationType type;
    private String title;
    private String message;
    private boolean read;
    private String issueId;
    private String projectId;
    private LocalDateTime createdAt;
}
