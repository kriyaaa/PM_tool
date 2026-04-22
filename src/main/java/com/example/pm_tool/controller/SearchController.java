package com.example.pm_tool.controller;

import com.example.pm_tool.dto.SearchResponse;
import com.example.pm_tool.service.IssueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
@Tag(name = "Search")
public class SearchController {

    private final IssueService issueService;

    @GetMapping
    @Operation(summary = "Search issues")
    public SearchResponse search(@RequestParam(required = false) String q,
                                 @RequestParam(required = false) String projectId,
                                 @RequestParam(required = false) String status,
                                 @RequestParam(required = false) String assigneeId,
                                 @RequestParam(required = false) String priority) {
        return issueService.search(q, projectId, status, assigneeId, priority);
    }
}
