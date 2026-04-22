package com.example.pm_tool.exception;

public class WorkflowViolationException extends RuntimeException {
    public WorkflowViolationException(String message) {
        super(message);
    }
}
