package com.example.pm_tool.service;

import com.example.pm_tool.dto.UserRequest;
import com.example.pm_tool.dto.UserResponse;
import com.example.pm_tool.entity.User;
import com.example.pm_tool.exception.ConflictException;
import com.example.pm_tool.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserResponse createUser(UserRequest request) {
        userRepository.findByEmailIgnoreCase(request.getEmail()).ifPresent(existing -> {
            throw new ConflictException("A user with this email already exists");
        });

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .build();
        return toResponse(userRepository.save(user));
    }

    public UserResponse getUser(String id) {
        return toResponse(userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found")));
    }

    public UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
}
