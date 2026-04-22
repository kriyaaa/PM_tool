package com.example.pm_tool.repository;

import com.example.pm_tool.entity.Sprint;
import com.example.pm_tool.entity.enums.SprintState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SprintRepository extends JpaRepository<Sprint, String> {

    List<Sprint> findByProjectIdOrderByStartDateAsc(String projectId);

    Optional<Sprint> findByProjectIdAndState(String projectId, SprintState state);
}
