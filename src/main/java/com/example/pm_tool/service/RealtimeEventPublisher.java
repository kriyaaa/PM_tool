package com.example.pm_tool.service;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class RealtimeEventPublisher {

    private final SimpMessagingTemplate messagingTemplate;

    public void projectEvent(String projectId, String type, Object payload) {
        Object event = Map.of(
                "type", type,
                "payload", payload
        );
        messagingTemplate.convertAndSend("/topic/projects/" + projectId, event);
    }

    public void issueEvent(String issueId, String type, Object payload) {
        Object event = Map.of(
                "type", type,
                "payload", payload
        );
        messagingTemplate.convertAndSend("/topic/issues/" + issueId, event);
    }

    public void presenceEvent(String projectId, Object payload) {
        messagingTemplate.convertAndSend("/topic/projects/" + projectId + "/presence", payload);
    }
}
