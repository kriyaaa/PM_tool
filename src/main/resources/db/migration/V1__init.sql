CREATE TABLE users (
    id VARCHAR(64) PRIMARY KEY,
    version BIGINT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    name VARCHAR(120) NOT NULL,
    email VARCHAR(160) NOT NULL UNIQUE
);

CREATE TABLE projects (
    id VARCHAR(64) PRIMARY KEY,
    version BIGINT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    name VARCHAR(140) NOT NULL,
    project_key VARCHAR(20) NOT NULL UNIQUE,
    description VARCHAR(1000),
    owner_id VARCHAR(64) NOT NULL,
    CONSTRAINT fk_projects_owner FOREIGN KEY (owner_id) REFERENCES users (id)
);

CREATE TABLE project_statuses (
    id VARCHAR(64) PRIMARY KEY,
    version BIGINT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    project_id VARCHAR(64) NOT NULL,
    name VARCHAR(60) NOT NULL,
    display_order INT NOT NULL,
    category VARCHAR(30) NOT NULL,
    CONSTRAINT fk_status_project FOREIGN KEY (project_id) REFERENCES projects (id),
    CONSTRAINT uk_status_name UNIQUE (project_id, name),
    CONSTRAINT uk_status_order UNIQUE (project_id, display_order)
);

CREATE TABLE workflow_transitions (
    id VARCHAR(64) PRIMARY KEY,
    version BIGINT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    project_id VARCHAR(64) NOT NULL,
    from_status_id VARCHAR(64) NOT NULL,
    to_status_id VARCHAR(64) NOT NULL,
    require_assignee BIT NOT NULL,
    auto_assign_reviewer BIT NOT NULL,
    CONSTRAINT fk_transition_project FOREIGN KEY (project_id) REFERENCES projects (id),
    CONSTRAINT fk_transition_from_status FOREIGN KEY (from_status_id) REFERENCES project_statuses (id),
    CONSTRAINT fk_transition_to_status FOREIGN KEY (to_status_id) REFERENCES project_statuses (id),
    CONSTRAINT uk_transition UNIQUE (project_id, from_status_id, to_status_id)
);

CREATE TABLE sprints (
    id VARCHAR(64) PRIMARY KEY,
    version BIGINT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    name VARCHAR(140) NOT NULL,
    goal VARCHAR(1000),
    start_date DATE,
    end_date DATE,
    state VARCHAR(30) NOT NULL,
    project_id VARCHAR(64) NOT NULL,
    CONSTRAINT fk_sprint_project FOREIGN KEY (project_id) REFERENCES projects (id)
);

CREATE TABLE issues (
    id VARCHAR(64) PRIMARY KEY,
    version BIGINT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    issue_key VARCHAR(40) NOT NULL UNIQUE,
    title VARCHAR(200) NOT NULL,
    description VARCHAR(4000),
    type VARCHAR(30) NOT NULL,
    status VARCHAR(60) NOT NULL,
    priority VARCHAR(30) NOT NULL,
    story_points INT,
    project_id VARCHAR(64) NOT NULL,
    assignee_id VARCHAR(64),
    reporter_id VARCHAR(64),
    reviewer_id VARCHAR(64),
    sprint_id VARCHAR(64),
    parent_id VARCHAR(64),
    CONSTRAINT fk_issue_project FOREIGN KEY (project_id) REFERENCES projects (id),
    CONSTRAINT fk_issue_assignee FOREIGN KEY (assignee_id) REFERENCES users (id),
    CONSTRAINT fk_issue_reporter FOREIGN KEY (reporter_id) REFERENCES users (id),
    CONSTRAINT fk_issue_reviewer FOREIGN KEY (reviewer_id) REFERENCES users (id),
    CONSTRAINT fk_issue_sprint FOREIGN KEY (sprint_id) REFERENCES sprints (id),
    CONSTRAINT fk_issue_parent FOREIGN KEY (parent_id) REFERENCES issues (id)
);

CREATE TABLE issue_labels (
    issue_id VARCHAR(64) NOT NULL,
    label VARCHAR(50) NOT NULL,
    CONSTRAINT fk_issue_label_issue FOREIGN KEY (issue_id) REFERENCES issues (id)
);

CREATE TABLE issue_watchers (
    issue_id VARCHAR(64) NOT NULL,
    user_id VARCHAR(64) NOT NULL,
    PRIMARY KEY (issue_id, user_id),
    CONSTRAINT fk_issue_watchers_issue FOREIGN KEY (issue_id) REFERENCES issues (id),
    CONSTRAINT fk_issue_watchers_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE comments (
    id VARCHAR(64) PRIMARY KEY,
    version BIGINT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    issue_id VARCHAR(64) NOT NULL,
    author_id VARCHAR(64) NOT NULL,
    parent_comment_id VARCHAR(64),
    content VARCHAR(4000) NOT NULL,
    CONSTRAINT fk_comment_issue FOREIGN KEY (issue_id) REFERENCES issues (id),
    CONSTRAINT fk_comment_author FOREIGN KEY (author_id) REFERENCES users (id),
    CONSTRAINT fk_comment_parent FOREIGN KEY (parent_comment_id) REFERENCES comments (id)
);

CREATE TABLE activity_logs (
    id VARCHAR(64) PRIMARY KEY,
    version BIGINT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    project_id VARCHAR(64) NOT NULL,
    issue_id VARCHAR(64),
    actor_id VARCHAR(64),
    type VARCHAR(50) NOT NULL,
    details VARCHAR(4000) NOT NULL,
    CONSTRAINT fk_activity_project FOREIGN KEY (project_id) REFERENCES projects (id),
    CONSTRAINT fk_activity_issue FOREIGN KEY (issue_id) REFERENCES issues (id),
    CONSTRAINT fk_activity_actor FOREIGN KEY (actor_id) REFERENCES users (id)
);

CREATE TABLE notifications (
    id VARCHAR(64) PRIMARY KEY,
    version BIGINT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    recipient_id VARCHAR(64) NOT NULL,
    project_id VARCHAR(64) NOT NULL,
    issue_id VARCHAR(64),
    type VARCHAR(40) NOT NULL,
    title VARCHAR(255) NOT NULL,
    message VARCHAR(1000) NOT NULL,
    is_read BIT NOT NULL,
    CONSTRAINT fk_notification_recipient FOREIGN KEY (recipient_id) REFERENCES users (id),
    CONSTRAINT fk_notification_project FOREIGN KEY (project_id) REFERENCES projects (id),
    CONSTRAINT fk_notification_issue FOREIGN KEY (issue_id) REFERENCES issues (id)
);

CREATE TABLE custom_field_definitions (
    id VARCHAR(64) PRIMARY KEY,
    version BIGINT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    project_id VARCHAR(64) NOT NULL,
    field_key VARCHAR(60) NOT NULL,
    name VARCHAR(120) NOT NULL,
    type VARCHAR(30) NOT NULL,
    required BIT NOT NULL,
    options_json VARCHAR(2000),
    CONSTRAINT fk_custom_field_project FOREIGN KEY (project_id) REFERENCES projects (id),
    CONSTRAINT uk_custom_field UNIQUE (project_id, field_key)
);

CREATE TABLE custom_field_values (
    id VARCHAR(64) PRIMARY KEY,
    version BIGINT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    issue_id VARCHAR(64) NOT NULL,
    definition_id VARCHAR(64) NOT NULL,
    field_value VARCHAR(2000) NOT NULL,
    CONSTRAINT fk_custom_value_issue FOREIGN KEY (issue_id) REFERENCES issues (id),
    CONSTRAINT fk_custom_value_definition FOREIGN KEY (definition_id) REFERENCES custom_field_definitions (id),
    CONSTRAINT uk_custom_value UNIQUE (issue_id, definition_id)
);

CREATE INDEX idx_issues_project_status ON issues (project_id, status);
CREATE INDEX idx_issues_project_assignee ON issues (project_id, assignee_id);
CREATE INDEX idx_issues_project_priority ON issues (project_id, priority);
CREATE INDEX idx_activity_logs_project_created ON activity_logs (project_id, created_at);
