package com.example.pm_tool.controller;

import com.example.pm_tool.dto.UserRequest;
import com.example.pm_tool.dto.UserResponse;
import com.example.pm_tool.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users")
public class UserController {

    private final UserService userService;

    @PostMapping
    @Operation(summary = "Create user")
    public UserResponse create(@Valid @RequestBody UserRequest request) {
        return userService.createUser(request);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user")
    public UserResponse get(@PathVariable String id) {
        return userService.getUser(id);
    }
}
