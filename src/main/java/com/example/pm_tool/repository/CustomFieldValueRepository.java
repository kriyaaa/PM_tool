package com.example.pm_tool.repository;

import com.example.pm_tool.entity.CustomFieldValue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomFieldValueRepository extends JpaRepository<CustomFieldValue, String> {
    List<CustomFieldValue> findByIssueId(String issueId);
}
