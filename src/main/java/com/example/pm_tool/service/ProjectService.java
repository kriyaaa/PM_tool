package com.example.pm_tool.service;

import com.example.pm_tool.dto.*;
import com.example.pm_tool.entity.*;
import com.example.pm_tool.entity.enums.StatusCategory;
import com.example.pm_tool.exception.ConflictException;
import com.example.pm_tool.exception.ResourceNotFoundException;
import com.example.pm_tool.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectStatusRepository projectStatusRepository;
    private final WorkflowTransitionRepository workflowTransitionRepository;
    private final IssueRepository issueRepository;
    private final ActivityService activityService;

    public ProjectResponse createProject(ProjectRequest request) {
        projectRepository.findByKeyIgnoreCase(request.getKey()).ifPresent(existing -> {
            throw new ConflictException("Project key already exists");
        });

        User owner = userRepository.findById(request.getOwnerId())
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));

        Project project = Project.builder()
                .name(request.getName())
                .key(request.getKey().toUpperCase())
                .description(request.getDescription())
                .owner(owner)
                .build();
        Project saved = projectRepository.save(project);

        ProjectStatus todo = createStatus(saved, "To Do", 1, StatusCategory.TODO);
        ProjectStatus inProgress = createStatus(saved, "In Progress", 2, StatusCategory.IN_PROGRESS);
        ProjectStatus inReview = createStatus(saved, "In Review", 3, StatusCategory.IN_PROGRESS);
        ProjectStatus done = createStatus(saved, "Done", 4, StatusCategory.DONE);

        createTransition(saved, todo, inProgress, false, false);
        createTransition(saved, inProgress, inReview, true, true);
        createTransition(saved, inReview, done, true, false);
        createTransition(saved, inReview, inProgress, true, false);
        createTransition(saved, inProgress, todo, false, false);

        return getProject(saved.getId());
    }

    public List<ProjectResponse> getProjects() {
        return projectRepository.findAll().stream().map(project -> getProject(project.getId())).toList();
    }

    public ProjectResponse getProject(String projectId) {
        Project project = getProjectEntity(projectId);
        return ProjectResponse.builder()
                .id(project.getId())
                .key(project.getKey())
                .name(project.getName())
                .description(project.getDescription())
                .ownerId(project.getOwner().getId())
                .ownerName(project.getOwner().getName())
                .statuses(projectStatusRepository.findByProjectIdOrderByDisplayOrderAsc(projectId)
                        .stream()
                        .map(status -> WorkflowStatusResponse.builder()
                                .id(status.getId())
                                .name(status.getName())
                                .category(status.getCategory().name())
                                .order(status.getDisplayOrder())
                                .build())
                        .toList())
                .build();
    }

    @Transactional(readOnly = true)
    public BoardResponse getBoard(String projectId) {
        Project project = getProjectEntity(projectId);
        List<ProjectStatus> statuses = projectStatusRepository.findByProjectIdOrderByDisplayOrderAsc(projectId);
        List<IssueResponse> issues = issueRepository.findByProjectIdOrderByCreatedAtDesc(projectId).stream()
                .map(this::toIssueResponse)
                .toList();

        Map<String, List<IssueResponse>> issuesByStatus = issues.stream()
                .collect(Collectors.groupingBy(IssueResponse::getStatus));

        List<BoardColumnResponse> columns = statuses.stream()
                .map(status -> BoardColumnResponse.builder()
                        .status(status.getName())
                        .category(status.getCategory().name())
                        .order(status.getDisplayOrder())
                        .issues(issuesByStatus.getOrDefault(status.getName(), List.of()))
                        .build())
                .toList();

        return BoardResponse.builder()
                .projectId(project.getId())
                .projectKey(project.getKey())
                .projectName(project.getName())
                .columns(columns)
                .build();
    }

    public List<ActivityResponse> getActivity(String projectId, int limit) {
        getProjectEntity(projectId);
        return activityService.getProjectActivity(projectId, limit);
    }

    public Project getProjectEntity(String projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
    }

    public ProjectStatus getStatus(String projectId, String statusName) {
        return projectStatusRepository.findByProjectIdAndNameIgnoreCase(projectId, statusName)
                .orElseThrow(() -> new ResourceNotFoundException("Status not found for project"));
    }

    public IssueResponse toIssueResponse(Issue issue) {
        return IssueResponse.builder()
                .id(issue.getId())
                .issueKey(issue.getIssueKey())
                .projectId(issue.getProject().getId())
                .type(issue.getType().name())
                .title(issue.getTitle())
                .description(issue.getDescription())
                .status(issue.getStatus())
                .priority(issue.getPriority())
                .storyPoints(issue.getStoryPoints())
                .assigneeId(issue.getAssignee() != null ? issue.getAssignee().getId() : null)
                .assigneeName(issue.getAssignee() != null ? issue.getAssignee().getName() : null)
                .reporterId(issue.getReporter() != null ? issue.getReporter().getId() : null)
                .reporterName(issue.getReporter() != null ? issue.getReporter().getName() : null)
                .reviewerId(issue.getReviewer() != null ? issue.getReviewer().getId() : null)
                .reviewerName(issue.getReviewer() != null ? issue.getReviewer().getName() : null)
                .sprintId(issue.getSprint() != null ? issue.getSprint().getId() : null)
                .sprintName(issue.getSprint() != null ? issue.getSprint().getName() : null)
                .parentId(issue.getParent() != null ? issue.getParent().getId() : null)
                .labels(new LinkedHashSet<>(issue.getLabels()))
                .watcherIds(issue.getWatchers().stream().map(User::getId).collect(Collectors.toCollection(java.util.LinkedHashSet::new)))
                .createdAt(issue.getCreatedAt())
                .updatedAt(issue.getUpdatedAt())
                .build();
    }

    private ProjectStatus createStatus(Project project, String name, int order, StatusCategory category) {
        ProjectStatus status = ProjectStatus.builder()
                .project(project)
                .name(name)
                .displayOrder(order)
                .category(category)
                .build();
        return projectStatusRepository.save(status);
    }

    private void createTransition(Project project, ProjectStatus from, ProjectStatus to, boolean requireAssignee, boolean autoAssignReviewer) {
        workflowTransitionRepository.save(WorkflowTransition.builder()
                .project(project)
                .fromStatus(from)
                .toStatus(to)
                .requireAssignee(requireAssignee)
                .autoAssignReviewer(autoAssignReviewer)
                .build());
    }
}
