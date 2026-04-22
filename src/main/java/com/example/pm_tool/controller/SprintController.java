package com.example.pm_tool.controller;

import com.example.pm_tool.dto.SprintCompletionRequest;
import com.example.pm_tool.dto.SprintCompletionResponse;
import com.example.pm_tool.dto.SprintResponse;
import com.example.pm_tool.service.SprintService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sprints")
@RequiredArgsConstructor
@Tag(name = "Sprints")
public class SprintController {

    private final SprintService sprintService;

    @PostMapping("/{id}/start")
    @Operation(summary = "Start sprint")
    public SprintResponse start(@PathVariable String id) {
        return sprintService.startSprint(id);
    }

    @PostMapping("/{id}/complete")
    @Operation(summary = "Complete sprint")
    public SprintCompletionResponse complete(@PathVariable String id,
                                             @Valid @RequestBody SprintCompletionRequest request) {
        return sprintService.completeSprint(id, request);
    }
}
