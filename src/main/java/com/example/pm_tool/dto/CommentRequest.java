package com.example.pm_tool.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CommentRequest {

    @NotBlank
    private String authorId;

    private String parentCommentId;

    @NotBlank
    private String content;
}
