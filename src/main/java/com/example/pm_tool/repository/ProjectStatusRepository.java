package com.example.pm_tool.repository;

import com.example.pm_tool.entity.ProjectStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectStatusRepository extends JpaRepository<ProjectStatus, String> {

    List<ProjectStatus> findByProjectIdOrderByDisplayOrderAsc(String projectId);

    Optional<ProjectStatus> findByProjectIdAndNameIgnoreCase(String projectId, String name);
}
