package com.example.pm_tool.controller;

import com.example.pm_tool.dto.*;
import com.example.pm_tool.service.IssueService;
import com.example.pm_tool.service.ProjectService;
import com.example.pm_tool.service.SprintService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
@Tag(name = "Projects")
public class ProjectController {

    private final ProjectService projectService;
    private final IssueService issueService;
    private final SprintService sprintService;

    @PostMapping
    @Operation(summary = "Create project")
    public ProjectResponse createProject(@Valid @RequestBody ProjectRequest request) {
        return projectService.createProject(request);
    }

    @GetMapping
    @Operation(summary = "List projects")
    public List<ProjectResponse> getProjects() {
        return projectService.getProjects();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get project")
    public ProjectResponse getProject(@PathVariable String id) {
        return projectService.getProject(id);
    }

    @PostMapping("/{id}/issues")
    @Operation(summary = "Create issue in project")
    public IssueResponse createIssue(@PathVariable String id, @Valid @RequestBody CreateIssueRequest request) {
        return issueService.createIssue(id, request);
    }

    @GetMapping("/{id}/board")
    @Operation(summary = "Get board state")
    public BoardResponse getBoard(@PathVariable String id) {
        return projectService.getBoard(id);
    }

    @GetMapping("/{id}/sprints")
    @Operation(summary = "List sprints for project")
    public List<SprintResponse> getSprints(@PathVariable String id) {
        return sprintService.listByProject(id);
    }

    @PostMapping("/{id}/sprints")
    @Operation(summary = "Create sprint for project")
    public SprintResponse createSprint(@PathVariable String id, @Valid @RequestBody SprintRequest request) {
        return sprintService.createSprint(id, request);
    }

    @GetMapping("/{id}/activity")
    @Operation(summary = "Get project activity feed")
    public List<ActivityResponse> getActivity(@PathVariable String id,
                                              @RequestParam(defaultValue = "25") int limit) {
        return projectService.getActivity(id, limit);
    }
}
