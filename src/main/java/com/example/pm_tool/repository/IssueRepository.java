package com.example.pm_tool.repository;

import com.example.pm_tool.entity.Issue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface IssueRepository extends JpaRepository<Issue, String>, JpaSpecificationExecutor<Issue> {

    Optional<Issue> findByIssueKeyIgnoreCase(String issueKey);

    List<Issue> findByProjectIdOrderByCreatedAtDesc(String projectId);

    long countByProjectId(String projectId);
}
