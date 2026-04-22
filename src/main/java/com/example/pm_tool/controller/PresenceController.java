package com.example.pm_tool.controller;

import com.example.pm_tool.service.PresenceService;
import com.example.pm_tool.service.RealtimeEventPublisher;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/presence")
@RequiredArgsConstructor
@Tag(name = "Presence")
public class PresenceController {

    private final PresenceService presenceService;
    private final RealtimeEventPublisher realtimeEventPublisher;

    @PostMapping("/{projectId}/join")
    @Operation(summary = "Join presence for project")
    public Map<String, LocalDateTime> join(@PathVariable String projectId, @RequestParam String userId) {
        Map<String, LocalDateTime> snapshot = presenceService.join(projectId, userId);
        realtimeEventPublisher.presenceEvent(projectId, snapshot);
        return snapshot;
    }

    @PostMapping("/{projectId}/leave")
    @Operation(summary = "Leave presence for project")
    public Map<String, LocalDateTime> leave(@PathVariable String projectId, @RequestParam String userId) {
        Map<String, LocalDateTime> snapshot = presenceService.leave(projectId, userId);
        realtimeEventPublisher.presenceEvent(projectId, snapshot);
        return snapshot;
    }

    @GetMapping("/{projectId}")
    @Operation(summary = "Get presence snapshot for project")
    public Map<String, LocalDateTime> snapshot(@PathVariable String projectId) {
        return presenceService.snapshot(projectId);
    }
}
