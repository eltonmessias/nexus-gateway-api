package com.nexus.nexusgateway.ratelimit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexus.nexusiam.domain.port.out.ApiClientRepository;
import com.nexus.nexusiam.infrastructure.security.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final ApiClientRepository apiClientRepository;
    private final RateLimitService rateLimitService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            String tokenType = jwtService.extractTokenType(token);

            if (!"client_access".equals(tokenType)) {
                filterChain.doFilter(request, response);
                return;
            }

            String clientId = jwtService.extractEmail(token);

            apiClientRepository.findByClientId(clientId).ifPresent(client -> {
                RateLimitResult result = rateLimitService.check(clientId, client.getRateLimitRpm(), client.getRateLimitBurst());

                response.setHeader("X-RateLimit-Limit", String.valueOf(result.limit()));
                response.setHeader("X-RateLimit-Remaining", String.valueOf(Math.max(0, result.limit() - result.currentCount())));

                if (!result.allowed()) {
                    response.setHeader("Retry-After", String.valueOf(result.retryAfterMs() / 1000));
                    try {
                        writeErrorResponse(response, HttpStatus.TOO_MANY_REQUESTS);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });

            if (response.isCommitted()) return;

        } catch (Exception e) {
            // falha no Redis não deve bloquear o pedido
        }

        filterChain.doFilter(request, response);
    }

    private void writeErrorResponse(HttpServletResponse response, HttpStatus status) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), Map.of(
                "status", status.value(),
                "code", "RATE_LIMIT_EXCEEDED",
                "message", "Too many requests. Please retry after the indicated time.",
                "timestamp", Instant.now().toString()
        ));
    }
}
