package com.example.pm_tool.repository;

import com.example.pm_tool.entity.CustomFieldDefinition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomFieldDefinitionRepository extends JpaRepository<CustomFieldDefinition, String> {
    List<CustomFieldDefinition> findByProjectIdOrderByNameAsc(String projectId);
}
