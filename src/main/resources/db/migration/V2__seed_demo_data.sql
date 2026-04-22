INSERT INTO users (id, version, created_at, updated_at, name, email)
VALUES ('u1', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Harry', 'harry@example.com');

INSERT INTO projects (id, version, created_at, updated_at, name, project_key, description, owner_id)
VALUES ('p1', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'PM Tool', 'PMT', 'Demo workspace for the take-home assignment', 'u1');

INSERT INTO project_statuses (id, version, created_at, updated_at, project_id, name, display_order, category)
VALUES
('status_todo', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'p1', 'To Do', 1, 'TODO'),
('status_in_progress', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'p1', 'In Progress', 2, 'IN_PROGRESS'),
('status_in_review', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'p1', 'In Review', 3, 'IN_PROGRESS'),
('status_done', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'p1', 'Done', 4, 'DONE');

INSERT INTO workflow_transitions (id, version, created_at, updated_at, project_id, from_status_id, to_status_id, require_assignee, auto_assign_reviewer)
VALUES
('wf_1', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'p1', 'status_todo', 'status_in_progress', false, false),
('wf_2', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'p1', 'status_in_progress', 'status_in_review', true, true),
('wf_3', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'p1', 'status_in_review', 'status_done', true, false),
('wf_4', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'p1', 'status_in_review', 'status_in_progress', true, false),
('wf_5', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'p1', 'status_in_progress', 'status_todo', false, false);
