package com.example.pm_tool.service;

import com.example.pm_tool.dto.ActivityResponse;
import com.example.pm_tool.entity.ActivityLog;
import com.example.pm_tool.entity.Issue;
import com.example.pm_tool.entity.Project;
import com.example.pm_tool.entity.User;
import com.example.pm_tool.entity.enums.ActivityType;
import com.example.pm_tool.repository.ActivityLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ActivityService {

    private final ActivityLogRepository activityLogRepository;

    public void log(Project project, Issue issue, User actor, ActivityType type, String details) {
        ActivityLog log = ActivityLog.builder()
                .project(project)
                .issue(issue)
                .actor(actor)
                .type(type)
                .details(details)
                .build();
        activityLogRepository.save(log);
    }

    public List<ActivityResponse> getProjectActivity(String projectId, int limit) {
        return activityLogRepository.findByProjectIdOrderByCreatedAtDesc(projectId, PageRequest.of(0, limit))
                .stream()
                .map(log -> ActivityResponse.builder()
                        .id(log.getId())
                        .type(log.getType())
                        .details(log.getDetails())
                        .actorId(log.getActor() != null ? log.getActor().getId() : null)
                        .actorName(log.getActor() != null ? log.getActor().getName() : null)
                        .issueId(log.getIssue() != null ? log.getIssue().getId() : null)
                        .issueKey(log.getIssue() != null ? log.getIssue().getIssueKey() : null)
                        .createdAt(log.getCreatedAt())
                        .build())
                .toList();
    }
}
