package com.example.pm_tool.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI pmToolOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("PM Tool API")
                        .description("Jira-like backend for projects, issues, workflow, sprints, comments, activity, notifications, and real-time collaboration.")
                        .version("1.0.0")
                        .contact(new Contact().name("PM Tool Demo")));
    }
}
