package com.nexus.nexusiam.infrastructure.config;

import com.nexus.nexusiam.domain.port.out.*;
import com.nexus.nexusiam.domain.service.*;
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

    @Bean
    public ApiClientService apiClientService(ApiClientRepository apiClientRepository) {
        return new ApiClientService(apiClientRepository);
    }

    @Bean
    public TeamMemberService teamMemberService(
            TeamMemberRepository teamMemberRepository,
            TeamRepository teamRepository,
            OrgMemberRepository orgMemberRepository) {
        return new TeamMemberService(teamMemberRepository, teamRepository, orgMemberRepository);
    }
}
