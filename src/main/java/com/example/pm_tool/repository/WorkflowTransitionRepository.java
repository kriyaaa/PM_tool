package com.example.pm_tool.repository;

import com.example.pm_tool.entity.WorkflowTransition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkflowTransitionRepository extends JpaRepository<WorkflowTransition, String> {

    List<WorkflowTransition> findByProjectIdAndFromStatus_NameIgnoreCase(String projectId, String fromStatusName);

    Optional<WorkflowTransition> findByProjectIdAndFromStatus_NameIgnoreCaseAndToStatus_NameIgnoreCase(
            String projectId,
            String fromStatusName,
            String toStatusName
    );
}
