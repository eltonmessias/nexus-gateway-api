package com.nexus.nexusiam.infrastructure.config;

import com.nexus.nexusiam.domain.port.out.OrganizationRepository;
import com.nexus.nexusiam.domain.port.out.ProjectRepository;
import com.nexus.nexusiam.domain.port.out.TeamRepository;
import com.nexus.nexusiam.domain.port.out.UserRepository;
import com.nexus.nexusiam.domain.service.OrganizationService;
import com.nexus.nexusiam.domain.service.ProjectService;
import com.nexus.nexusiam.domain.service.TeamService;
import com.nexus.nexusiam.domain.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IamConfig {

    @Bean
    public OrganizationService organizationService(OrganizationRepository organizationRepository) {
        return new OrganizationService(organizationRepository);
    }

    @Bean
    public UserService userService(UserRepository userRepository) {
        return new UserService(userRepository);
    }

    @Bean
    public TeamService teamService(TeamRepository teamRepository) {
        return new TeamService(teamRepository);
    }

    @Bean
    public ProjectService projectService(ProjectRepository projectRepository) {
        return new ProjectService(projectRepository);
    }
}
