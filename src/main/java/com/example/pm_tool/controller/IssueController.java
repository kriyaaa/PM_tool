package com.example.pm_tool.controller;

import com.example.pm_tool.dto.*;
import com.example.pm_tool.service.CommentService;
import com.example.pm_tool.service.IssueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/issues")
@RequiredArgsConstructor
@Tag(name = "Issues")
public class IssueController {

    private final IssueService issueService;
    private final CommentService commentService;

    @GetMapping("/{id}")
    @Operation(summary = "Get issue")
    public IssueResponse get(@PathVariable String id) {
        return issueService.getIssue(id);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update issue fields")
    public IssueResponse update(@PathVariable String id, @RequestBody IssueUpdateRequest request) {
        return issueService.updateIssue(id, request);
    }

    @PostMapping("/{id}/transitions")
    @Operation(summary = "Transition issue workflow status")
    public IssueResponse transition(@PathVariable String id, @Valid @RequestBody IssueTransitionRequest request) {
        return issueService.transition(id, request);
    }

    @GetMapping("/{id}/comments")
    @Operation(summary = "List comments for issue")
    public List<CommentResponse> listComments(@PathVariable String id) {
        return commentService.listByIssue(id);
    }

    @PostMapping("/{id}/comments")
    @Operation(summary = "Add comment to issue")
    public CommentResponse addComment(@PathVariable String id, @Valid @RequestBody CommentRequest request) {
        return commentService.addComment(id, request);
    }

    @PostMapping("/{id}/watch")
    @Operation(summary = "Watch issue")
    public IssueResponse watch(@PathVariable String id, @RequestParam String userId) {
        return issueService.watchIssue(id, userId);
    }

    @DeleteMapping("/{id}/watch")
    @Operation(summary = "Unwatch issue")
    public IssueResponse unwatch(@PathVariable String id, @RequestParam String userId) {
        return issueService.unwatchIssue(id, userId);
    }
}
