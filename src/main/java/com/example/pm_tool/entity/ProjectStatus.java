package com.example.pm_tool.entity;

import com.example.pm_tool.entity.enums.StatusCategory;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "project_statuses", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"project_id", "name"}),
        @UniqueConstraint(columnNames = {"project_id", "display_order"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectStatus extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(nullable = false, length = 60)
    private String name;

    @Column(nullable = false)
    private Integer displayOrder;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StatusCategory category;
}
