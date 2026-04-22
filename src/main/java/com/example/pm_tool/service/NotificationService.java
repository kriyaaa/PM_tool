package com.example.pm_tool.service;

import com.example.pm_tool.dto.NotificationResponse;
import com.example.pm_tool.entity.Issue;
import com.example.pm_tool.entity.Notification;
import com.example.pm_tool.entity.Project;
import com.example.pm_tool.entity.User;
import com.example.pm_tool.entity.enums.NotificationType;
import com.example.pm_tool.repository.NotificationRepository;
import com.example.pm_tool.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public void notifyUser(User recipient, Project project, Issue issue, NotificationType type, String title, String message) {
        if (recipient == null) {
            return;
        }

        Notification notification = Notification.builder()
                .recipient(recipient)
                .project(project)
                .issue(issue)
                .type(type)
                .title(title)
                .message(message)
                .read(false)
                .build();
        notificationRepository.save(notification);
    }

    public List<NotificationResponse> getNotifications(String userId) {
        userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        return notificationRepository.findByRecipientIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(notification -> NotificationResponse.builder()
                        .id(notification.getId())
                        .type(notification.getType())
                        .title(notification.getTitle())
                        .message(notification.getMessage())
                        .read(notification.isRead())
                        .issueId(notification.getIssue() != null ? notification.getIssue().getId() : null)
                        .projectId(notification.getProject().getId())
                        .createdAt(notification.getCreatedAt())
                        .build())
                .toList();
    }
}
