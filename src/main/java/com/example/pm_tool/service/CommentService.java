package com.example.pm_tool.service;

import com.example.pm_tool.dto.CommentRequest;
import com.example.pm_tool.dto.CommentResponse;
import com.example.pm_tool.entity.Comment;
import com.example.pm_tool.entity.Issue;
import com.example.pm_tool.entity.User;
import com.example.pm_tool.entity.enums.ActivityType;
import com.example.pm_tool.entity.enums.NotificationType;
import com.example.pm_tool.exception.ResourceNotFoundException;
import com.example.pm_tool.repository.CommentRepository;
import com.example.pm_tool.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final IssueService issueService;
    private final ActivityService activityService;
    private final NotificationService notificationService;
    private final RealtimeEventPublisher realtimeEventPublisher;

    public List<CommentResponse> listByIssue(String issueId) {
        issueService.getIssueEntity(issueId);
        return commentRepository.findByIssueIdOrderByCreatedAtAsc(issueId).stream().map(this::toResponse).toList();
    }

    @Transactional
    public CommentResponse addComment(String issueId, CommentRequest request) {
        Issue issue = issueService.getIssueEntity(issueId);
        User author = userRepository.findById(request.getAuthorId())
                .orElseThrow(() -> new ResourceNotFoundException("Author not found"));
        Comment parent = request.getParentCommentId() != null && !request.getParentCommentId().isBlank()
                ? commentRepository.findById(request.getParentCommentId())
                .orElseThrow(() -> new ResourceNotFoundException("Parent comment not found"))
                : null;

        Comment comment = Comment.builder()
                .issue(issue)
                .author(author)
                .parentComment(parent)
                .content(request.getContent())
                .build();
        Comment saved = commentRepository.save(comment);

        activityService.log(issue.getProject(), issue, author, ActivityType.COMMENT_ADDED,
                "Comment added to " + issue.getIssueKey());
        notificationService.notifyUser(issue.getAssignee(), issue.getProject(), issue, NotificationType.COMMENT_ADDED,
                "New comment", "A new comment was added to " + issue.getIssueKey());
        issueService.notifyMentions(issue, saved.getContent());
        realtimeEventPublisher.projectEvent(issue.getProject().getId(), "comment_added", toResponse(saved));
        realtimeEventPublisher.issueEvent(issue.getId(), "comment_added", toResponse(saved));
        return toResponse(saved);
    }

    private CommentResponse toResponse(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .authorId(comment.getAuthor().getId())
                .authorName(comment.getAuthor().getName())
                .parentCommentId(comment.getParentComment() != null ? comment.getParentComment().getId() : null)
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
