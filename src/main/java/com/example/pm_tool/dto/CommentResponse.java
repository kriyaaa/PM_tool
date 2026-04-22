package com.example.pm_tool.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentResponse {
    private String id;
    private String content;
    private String authorId;
    private String authorName;
    private String parentCommentId;
    private LocalDateTime createdAt;
}
