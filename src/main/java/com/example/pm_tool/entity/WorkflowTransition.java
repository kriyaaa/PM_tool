package com.example.pm_tool.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "workflow_transitions", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"project_id", "from_status_id", "to_status_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkflowTransition extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne
    @JoinColumn(name = "from_status_id", nullable = false)
    private ProjectStatus fromStatus;

    @ManyToOne
    @JoinColumn(name = "to_status_id", nullable = false)
    private ProjectStatus toStatus;

    @Column(nullable = false)
    private boolean requireAssignee;

    @Column(nullable = false)
    private boolean autoAssignReviewer;
}
