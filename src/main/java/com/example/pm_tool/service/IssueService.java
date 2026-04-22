package com.example.pm_tool.service;

import com.example.pm_tool.dto.*;
import com.example.pm_tool.entity.*;
import com.example.pm_tool.entity.enums.*;
import com.example.pm_tool.exception.ResourceNotFoundException;
import com.example.pm_tool.exception.WorkflowViolationException;
import com.example.pm_tool.repository.*;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class IssueService {

    private static final Pattern MENTION_PATTERN = Pattern.compile("@([A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+)");

    private final IssueRepository issueRepository;
    private final UserRepository userRepository;
    private final SprintRepository sprintRepository;
    private final ProjectService projectService;
    private final WorkflowTransitionRepository workflowTransitionRepository;
    private final ActivityService activityService;
    private final NotificationService notificationService;
    private final RealtimeEventPublisher realtimeEventPublisher;

    @Transactional
    public IssueResponse createIssue(String projectId, CreateIssueRequest request) {
        Project project = projectService.getProjectEntity(projectId);
        User reporter = getUser(request.getReporterId(), "Reporter not found");
        User assignee = request.getAssigneeId() != null ? getUser(request.getAssigneeId(), "Assignee not found") : null;
        User reviewer = request.getReviewerId() != null ? getUser(request.getReviewerId(), "Reviewer not found") : null;
        Issue parent = request.getParentId() != null ? getIssueEntity(request.getParentId()) : null;
        Sprint sprint = request.getSprintId() != null ? getSprint(request.getSprintId()) : null;
        ProjectStatus defaultStatus = projectService.getStatus(projectId, "To Do");

        Issue issue = Issue.builder()
                .issueKey(nextIssueKey(project))
                .project(project)
                .type(IssueType.valueOf(request.getType().toUpperCase(Locale.ROOT)))
                .title(request.getTitle())
                .description(request.getDescription())
                .status(defaultStatus.getName())
                .priority(Priority.valueOf(request.getPriority().toUpperCase(Locale.ROOT)))
                .storyPoints(request.getStoryPoints())
                .reporter(reporter)
                .assignee(assignee)
                .reviewer(reviewer)
                .parent(parent)
                .sprint(sprint)
                .labels(normalizeLabels(request.getLabels()))
                .build();

        if (assignee != null) {
            issue.getWatchers().add(assignee);
        }
        issue.getWatchers().add(reporter);

        Issue saved = issueRepository.save(issue);
        activityService.log(project, saved, reporter, ActivityType.ISSUE_CREATED, "Created issue " + saved.getIssueKey());
        notificationService.notifyUser(assignee, project, saved, NotificationType.ASSIGNED,
                "Issue assigned", "You were assigned to " + saved.getIssueKey());
        realtimeEventPublisher.projectEvent(projectId, "issue_created", projectService.toIssueResponse(saved));
        realtimeEventPublisher.issueEvent(saved.getId(), "issue_created", projectService.toIssueResponse(saved));
        return projectService.toIssueResponse(saved);
    }

    @Transactional(readOnly = true)
    public IssueResponse getIssue(String id) {
        return projectService.toIssueResponse(getIssueEntity(id));
    }

    @Transactional
    public IssueResponse updateIssue(String id, IssueUpdateRequest request) {
        Issue issue = getIssueEntity(id);
        if (request.getTitle() != null) {
            issue.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            issue.setDescription(request.getDescription());
        }
        if (request.getPriority() != null) {
            issue.setPriority(Priority.valueOf(request.getPriority().toUpperCase(Locale.ROOT)));
        }
        if (request.getStoryPoints() != null) {
            issue.setStoryPoints(request.getStoryPoints());
        }
        if (request.getAssigneeId() != null) {
            User assignee = getUser(request.getAssigneeId(), "Assignee not found");
            issue.setAssignee(assignee);
            issue.getWatchers().add(assignee);
            notificationService.notifyUser(assignee, issue.getProject(), issue, NotificationType.ASSIGNED,
                    "Issue assigned", "You were assigned to " + issue.getIssueKey());
        }
        if (request.getReviewerId() != null) {
            issue.setReviewer(getUser(request.getReviewerId(), "Reviewer not found"));
        }
        if (request.getSprintId() != null) {
            issue.setSprint(getSprint(request.getSprintId()));
        }
        if (request.getLabels() != null) {
            issue.setLabels(normalizeLabels(request.getLabels()));
        }

        Issue saved = issueRepository.save(issue);
        activityService.log(saved.getProject(), saved, saved.getReporter(), ActivityType.ISSUE_UPDATED,
                "Updated issue " + saved.getIssueKey());
        realtimeEventPublisher.projectEvent(saved.getProject().getId(), "issue_updated", projectService.toIssueResponse(saved));
        return projectService.toIssueResponse(saved);
    }

    @Transactional
    public IssueResponse transition(String issueId, IssueTransitionRequest request) {
        Issue issue = getIssueEntity(issueId);
        User actor = getUser(request.getUserId(), "User not found");

        WorkflowTransition transition = workflowTransitionRepository
                .findByProjectIdAndFromStatus_NameIgnoreCaseAndToStatus_NameIgnoreCase(
                        issue.getProject().getId(),
                        issue.getStatus(),
                        request.getToStatus()
                )
                .orElseThrow(() -> new WorkflowViolationException(
                        "Transition not allowed. Allowed transitions: " + allowedTransitionNames(issue)
                ));

        if (transition.isRequireAssignee() && issue.getAssignee() == null) {
            throw new WorkflowViolationException("Assignee is required before moving to " + request.getToStatus());
        }

        issue.setStatus(transition.getToStatus().getName());

        if (transition.isAutoAssignReviewer() && issue.getReviewer() == null) {
            issue.setReviewer(issue.getReporter());
        }

        Issue saved = issueRepository.save(issue);
        activityService.log(saved.getProject(), saved, actor, ActivityType.ISSUE_TRANSITIONED,
                "Moved issue " + saved.getIssueKey() + " to " + saved.getStatus());
        notificationService.notifyUser(saved.getAssignee(), saved.getProject(), saved, NotificationType.STATUS_CHANGED,
                "Issue status changed", saved.getIssueKey() + " moved to " + saved.getStatus());
        realtimeEventPublisher.projectEvent(saved.getProject().getId(), "issue_moved", projectService.toIssueResponse(saved));
        realtimeEventPublisher.issueEvent(saved.getId(), "issue_updated", projectService.toIssueResponse(saved));
        return projectService.toIssueResponse(saved);
    }

    @Transactional
    public IssueResponse watchIssue(String issueId, String userId) {
        Issue issue = getIssueEntity(issueId);
        User user = getUser(userId, "User not found");
        issue.getWatchers().add(user);
        Issue saved = issueRepository.save(issue);
        activityService.log(saved.getProject(), saved, user, ActivityType.WATCHED,
                user.getName() + " started watching " + saved.getIssueKey());
        return projectService.toIssueResponse(saved);
    }

    @Transactional
    public IssueResponse unwatchIssue(String issueId, String userId) {
        Issue issue = getIssueEntity(issueId);
        User user = getUser(userId, "User not found");
        issue.getWatchers().removeIf(existing -> existing.getId().equals(user.getId()));
        Issue saved = issueRepository.save(issue);
        activityService.log(saved.getProject(), saved, user, ActivityType.UNWATCHED,
                user.getName() + " stopped watching " + saved.getIssueKey());
        return projectService.toIssueResponse(saved);
    }

    @Transactional(readOnly = true)
    public SearchResponse search(String query, String projectId, String status, String assigneeId, String priority) {
        Specification<Issue> specification = (root, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (projectId != null && !projectId.isBlank()) {
                predicates.add(cb.equal(root.get("project").get("id"), projectId));
            }
            if (status != null && !status.isBlank()) {
                predicates.add(cb.equal(cb.lower(root.get("status")), status.toLowerCase(Locale.ROOT)));
            }
            if (assigneeId != null && !assigneeId.isBlank()) {
                predicates.add(cb.equal(root.get("assignee").get("id"), assigneeId));
            }
            if (priority != null && !priority.isBlank()) {
                predicates.add(cb.equal(root.get("priority"), Priority.valueOf(priority.toUpperCase(Locale.ROOT))));
            }
            if (query != null && !query.isBlank()) {
                String like = "%" + query.toLowerCase(Locale.ROOT) + "%";
                Predicate titleLike = cb.like(cb.lower(root.get("title")), like);
                Predicate descriptionLike = cb.like(cb.lower(root.get("description")), like);
                Predicate keyLike = cb.like(cb.lower(root.get("issueKey")), like);
                predicates.add(cb.or(titleLike, descriptionLike, keyLike));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        List<IssueResponse> items = issueRepository.findAll(specification).stream()
                .map(projectService::toIssueResponse)
                .toList();
        return SearchResponse.builder().items(items).total(items.size()).build();
    }

    @Transactional(readOnly = true)
    public Issue getIssueEntity(String id) {
        return issueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Issue not found"));
    }

    @Transactional
    public void notifyMentions(Issue issue, String content) {
        Matcher matcher = MENTION_PATTERN.matcher(content);
        while (matcher.find()) {
            String email = matcher.group(1);
            userRepository.findByEmailIgnoreCase(email).ifPresent(user ->
                    notificationService.notifyUser(user, issue.getProject(), issue, NotificationType.MENTIONED,
                            "You were mentioned", "You were mentioned on " + issue.getIssueKey())
            );
        }
    }

    private String nextIssueKey(Project project) {
        return project.getKey() + "-" + (issueRepository.countByProjectId(project.getId()) + 1);
    }

    private User getUser(String id, String message) {
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(message));
    }

    private Sprint getSprint(String id) {
        return sprintRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Sprint not found"));
    }

    private String allowedTransitionNames(Issue issue) {
        return workflowTransitionRepository.findByProjectIdAndFromStatus_NameIgnoreCase(issue.getProject().getId(), issue.getStatus())
                .stream()
                .map(transition -> transition.getToStatus().getName())
                .sorted()
                .toList()
                .toString();
    }

    private Set<String> normalizeLabels(Set<String> labels) {
        if (labels == null) {
            return new LinkedHashSet<>();
        }
        Set<String> normalized = new LinkedHashSet<>();
        for (String label : labels) {
            if (label != null && !label.isBlank()) {
                normalized.add(label.trim().toLowerCase(Locale.ROOT));
            }
        }
        return normalized;
    }
}
