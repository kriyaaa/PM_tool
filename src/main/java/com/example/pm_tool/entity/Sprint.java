package com.example.pm_tool.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import com.example.pm_tool.entity.enums.SprintState;

@Entity
@Table(name = "sprints")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sprint extends BaseEntity {

    @Column(nullable = false, length = 140)
    private String name;

    @Column(length = 1000)
    private String goal;

    private LocalDate startDate;
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private SprintState state;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;
}
