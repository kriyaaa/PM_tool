package com.example.pm_tool.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "custom_field_values", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"issue_id", "definition_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomFieldValue extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "issue_id", nullable = false)
    private Issue issue;

    @ManyToOne
    @JoinColumn(name = "definition_id", nullable = false)
    private CustomFieldDefinition definition;

    @Column(name = "field_value", nullable = false, length = 2000)
    private String value;
}
