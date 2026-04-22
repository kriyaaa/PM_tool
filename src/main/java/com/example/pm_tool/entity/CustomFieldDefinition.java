package com.example.pm_tool.entity;

import com.example.pm_tool.entity.enums.CustomFieldType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "custom_field_definitions", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"project_id", "field_key"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomFieldDefinition extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(name = "field_key", nullable = false, length = 60)
    private String fieldKey;

    @Column(nullable = false, length = 120)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CustomFieldType type;

    @Column(nullable = false)
    private boolean required;

    @Column(length = 2000)
    private String optionsJson;
}
