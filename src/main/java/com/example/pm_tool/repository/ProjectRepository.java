package com.example.pm_tool.repository;

import com.example.pm_tool.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, String> {
    Optional<Project> findByKeyIgnoreCase(String key);
}
