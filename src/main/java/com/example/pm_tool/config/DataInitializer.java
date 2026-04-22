package com.example.pm_tool.config;

import com.example.pm_tool.entity.Project;
import com.example.pm_tool.entity.ProjectStatus;
import com.example.pm_tool.entity.User;
import com.example.pm_tool.entity.WorkflowTransition;
import com.example.pm_tool.entity.enums.StatusCategory;
import com.example.pm_tool.repository.ProjectRepository;
import com.example.pm_tool.repository.ProjectStatusRepository;
import com.example.pm_tool.repository.UserRepository;
import com.example.pm_tool.repository.WorkflowTransitionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("local")
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final ProjectStatusRepository projectStatusRepository;
    private final WorkflowTransitionRepository workflowTransitionRepository;

    @Override
    public void run(String... args) {
        User demoUser = userRepository.findById("u1")
                .orElseGet(() -> {
                    User user = new User();
                    user.setId("u1");
                    user.setName("Harry");
                    user.setEmail("harry@example.com");
                    return userRepository.save(user);
                });

        Project project = projectRepository.findById("p1")
                .orElseGet(() -> {
                    Project newProject = new Project();
                    newProject.setId("p1");
                    newProject.setName("PM Tool");
                    newProject.setKey("PMT");
                    newProject.setDescription("Demo workspace for the take-home assignment");
                    newProject.setOwner(demoUser);
                    return projectRepository.save(newProject);
                });

        if (projectStatusRepository.findByProjectIdOrderByDisplayOrderAsc(project.getId()).isEmpty()) {
            ProjectStatus todo = projectStatusRepository.save(ProjectStatus.builder()
                    .project(project)
                    .name("To Do")
                    .displayOrder(1)
                    .category(StatusCategory.TODO)
                    .build());
            ProjectStatus inProgress = projectStatusRepository.save(ProjectStatus.builder()
                    .project(project)
                    .name("In Progress")
                    .displayOrder(2)
                    .category(StatusCategory.IN_PROGRESS)
                    .build());
            ProjectStatus inReview = projectStatusRepository.save(ProjectStatus.builder()
                    .project(project)
                    .name("In Review")
                    .displayOrder(3)
                    .category(StatusCategory.IN_PROGRESS)
                    .build());
            ProjectStatus done = projectStatusRepository.save(ProjectStatus.builder()
                    .project(project)
                    .name("Done")
                    .displayOrder(4)
                    .category(StatusCategory.DONE)
                    .build());

            workflowTransitionRepository.save(WorkflowTransition.builder()
                    .project(project)
                    .fromStatus(todo)
                    .toStatus(inProgress)
                    .requireAssignee(false)
                    .autoAssignReviewer(false)
                    .build());
            workflowTransitionRepository.save(WorkflowTransition.builder()
                    .project(project)
                    .fromStatus(inProgress)
                    .toStatus(inReview)
                    .requireAssignee(true)
                    .autoAssignReviewer(true)
                    .build());
            workflowTransitionRepository.save(WorkflowTransition.builder()
                    .project(project)
                    .fromStatus(inReview)
                    .toStatus(done)
                    .requireAssignee(true)
                    .autoAssignReviewer(false)
                    .build());
            workflowTransitionRepository.save(WorkflowTransition.builder()
                    .project(project)
                    .fromStatus(inReview)
                    .toStatus(inProgress)
                    .requireAssignee(true)
                    .autoAssignReviewer(false)
                    .build());
            workflowTransitionRepository.save(WorkflowTransition.builder()
                    .project(project)
                    .fromStatus(inProgress)
                    .toStatus(todo)
                    .requireAssignee(false)
                    .autoAssignReviewer(false)
                    .build());
        }
    }
}
