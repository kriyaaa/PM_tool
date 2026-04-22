package com.example.pm_tool.repository;

import com.example.pm_tool.entity.ActivityLog;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, String> {

    List<ActivityLog> findByProjectIdOrderByCreatedAtDesc(String projectId, Pageable pageable);
}
