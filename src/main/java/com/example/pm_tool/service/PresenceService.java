package com.example.pm_tool.service;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PresenceService {

    private final Map<String, Map<String, LocalDateTime>> projectPresence = new ConcurrentHashMap<>();

    public Map<String, LocalDateTime> join(String projectId, String userId) {
        projectPresence.computeIfAbsent(projectId, ignored -> new ConcurrentHashMap<>())
                .put(userId, LocalDateTime.now());
        return snapshot(projectId);
    }

    public Map<String, LocalDateTime> leave(String projectId, String userId) {
        projectPresence.computeIfAbsent(projectId, ignored -> new ConcurrentHashMap<>()).remove(userId);
        return snapshot(projectId);
    }

    public Map<String, LocalDateTime> snapshot(String projectId) {
        return new TreeMap<>(projectPresence.getOrDefault(projectId, Map.of()));
    }
}
