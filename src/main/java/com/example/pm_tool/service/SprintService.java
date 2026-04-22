package com.example.pm_tool.service;

import com.example.pm_tool.dto.IssueResponse;
import com.example.pm_tool.dto.SprintCompletionRequest;
import com.example.pm_tool.dto.SprintCompletionResponse;
import com.example.pm_tool.dto.SprintRequest;
import com.example.pm_tool.dto.SprintResponse;
import com.example.pm_tool.entity.Issue;
import com.example.pm_tool.entity.Project;
import com.example.pm_tool.entity.Sprint;
import com.example.pm_tool.entity.User;
import com.example.pm_tool.entity.enums.ActivityType;
import com.example.pm_tool.entity.enums.SprintState;
import com.example.pm_tool.exception.ConflictException;
import com.example.pm_tool.exception.ResourceNotFoundException;
import com.example.pm_tool.repository.IssueRepository;
import com.example.pm_tool.repository.SprintRepository;
import com.example.pm_tool.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SprintService {

    private final SprintRepository sprintRepository;
    private final IssueRepository issueRepository;
    private final UserRepository userRepository;
    private final ProjectService projectService;
    private final ActivityService activityService;
    private final RealtimeEventPublisher realtimeEventPublisher;

    public List<SprintResponse> listByProject(String projectId) {
        projectService.getProjectEntity(projectId);
        return sprintRepository.findByProjectIdOrderByStartDateAsc(projectId).stream().map(this::toResponse).toList();
    }

    @Transactional
    public SprintResponse createSprint(String projectId, SprintRequest request) {
        Project project = projectService.getProjectEntity(projectId);
        Sprint sprint = Sprint.builder()
                .project(project)
                .name(request.getName())
                .goal(request.getGoal())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .state(SprintState.PLANNED)
                .build();
        Sprint saved = sprintRepository.save(sprint);
        activityService.log(project, null, project.getOwner(), ActivityType.SPRINT_CREATED, "Created sprint " + saved.getName());
        realtimeEventPublisher.projectEvent(projectId, "sprint_updated", toResponse(saved));
        return toResponse(saved);
    }

    @Transactional
    public SprintResponse startSprint(String sprintId) {
        Sprint sprint = getSprintEntity(sprintId);
        sprintRepository.findByProjectIdAndState(sprint.getProject().getId(), SprintState.ACTIVE)
                .ifPresent(existing -> {
                    throw new ConflictException("Another sprint is already active for this project");
                });
        sprint.setState(SprintState.ACTIVE);
        Sprint saved = sprintRepository.save(sprint);
        activityService.log(saved.getProject(), null, saved.getProject().getOwner(), ActivityType.SPRINT_STARTED,
                "Started sprint " + saved.getName());
        realtimeEventPublisher.projectEvent(saved.getProject().getId(), "sprint_updated", toResponse(saved));
        return toResponse(saved);
    }

    @Transactional
    public SprintCompletionResponse completeSprint(String sprintId, SprintCompletionRequest request) {
        Sprint sprint = getSprintEntity(sprintId);
        User actor = userRepository.findById(request.getActorId())
                .orElseThrow(() -> new ResourceNotFoundException("Actor not found"));
        sprint.setState(SprintState.COMPLETED);
        sprintRepository.save(sprint);

        List<Issue> incompleteIssues = issueRepository.findByProjectIdOrderByCreatedAtDesc(sprint.getProject().getId())
                .stream()
                .filter(issue -> issue.getSprint() != null && sprintId.equals(issue.getSprint().getId()))
                .filter(issue -> !"Done".equalsIgnoreCase(issue.getStatus()))
                .toList();

        Sprint nextSprint = null;
        if (request.getNextSprintId() != null && !request.getNextSprintId().isBlank()) {
            nextSprint = getSprintEntity(request.getNextSprintId());
        }

        for (Issue issue : incompleteIssues) {
            if (request.getCarryOverIssueIds().contains(issue.getId())) {
                issue.setSprint(nextSprint);
            }
        }
        issueRepository.saveAll(incompleteIssues);

        int completedStoryPoints = issueRepository.findByProjectIdOrderByCreatedAtDesc(sprint.getProject().getId())
                .stream()
                .filter(issue -> issue.getSprint() != null && sprintId.equals(issue.getSprint().getId()))
                .filter(issue -> "Done".equalsIgnoreCase(issue.getStatus()))
                .map(Issue::getStoryPoints)
                .filter(java.util.Objects::nonNull)
                .mapToInt(Integer::intValue)
                .sum();

        activityService.log(sprint.getProject(), null, actor, ActivityType.SPRINT_COMPLETED,
                "Completed sprint " + sprint.getName());
        realtimeEventPublisher.projectEvent(sprint.getProject().getId(), "sprint_updated", toResponse(sprint));
        return SprintCompletionResponse.builder()
                .sprint(toResponse(sprint))
                .incompleteIssues(incompleteIssues.stream().map(projectService::toIssueResponse).toList())
                .completedStoryPoints(completedStoryPoints)
                .build();
    }

    public Sprint getSprintEntity(String sprintId) {
        return sprintRepository.findById(sprintId)
                .orElseThrow(() -> new ResourceNotFoundException("Sprint not found"));
    }

    public SprintResponse toResponse(Sprint sprint) {
        return SprintResponse.builder()
                .id(sprint.getId())
                .projectId(sprint.getProject().getId())
                .name(sprint.getName())
                .goal(sprint.getGoal())
                .state(sprint.getState().name())
                .startDate(sprint.getStartDate())
                .endDate(sprint.getEndDate())
                .build();
    }
}
