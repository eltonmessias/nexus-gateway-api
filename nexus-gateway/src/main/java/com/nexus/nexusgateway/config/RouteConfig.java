package com.nexus.nexusgateway.config;

import com.nexus.nexusgateway.filter.AuthenticationFilter;
import com.nexus.nexusgateway.filter.RateLimitFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteConfig {

    private final AuthenticationFilter authFilter;
    private final RateLimitFilter rateLimitFilter;

    public RouteConfig(AuthenticationFilter authFilter,
                       RateLimitFilter rateLimitFilter) {
        this.authFilter      = authFilter;
        this.rateLimitFilter = rateLimitFilter;
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        RateLimitFilter.Config defaultRateLimit = new RateLimitFilter.Config();
        defaultRateLimit.setLimit(100);

        RateLimitFilter.Config searchRateLimit = new RateLimitFilter.Config();
        searchRateLimit.setLimit(500);

        return builder.routes()

                .route("feature-flags", r -> r
                        .path("/api/v1/flags/**")
                        .filters(f -> f
                                .filter(authFilter.apply(new AuthenticationFilter.Config()))
                                .filter(rateLimitFilter.apply(defaultRateLimit))
                        )
                        .uri("http://localhost:8081"))

                .route("job-queue", r -> r
                        .path("/api/v1/jobs/**")
                        .filters(f -> f
                                .filter(authFilter.apply(new AuthenticationFilter.Config()))
                                .filter(rateLimitFilter.apply(defaultRateLimit))
                        )
                        .uri("http://localhost:8082"))

                .route("search-engine", r -> r
                        .path("/api/v1/search/**")
                        .filters(f -> f
                                .filter(authFilter.apply(new AuthenticationFilter.Config()))
                                .filter(rateLimitFilter.apply(searchRateLimit))
                        )
                        .uri("http://localhost:8083"))

                .build();
    }
}
