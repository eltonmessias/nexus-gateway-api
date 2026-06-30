package com.nexus.nexusiam.infrastructure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexus.nexusiam.domain.port.out.ApiClientRepository;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final ApiClientRepository apiClientRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(7);

        try {
            String tokenType = jwtService.extractTokenType(token);

            if ("refresh".equals(tokenType)) {
                writeErrorResponse(response, HttpStatus.UNAUTHORIZED, "INVALID_TOKEN", "Refresh token cannot be used for authentication");
                return;
            }

            if ("client_access".equals(tokenType)) {
                authenticateApiClient(request, token);
            } else {
                authenticateUser(request, token);
            }

        } catch (ExpiredJwtException e) {
            writeErrorResponse(response, HttpStatus.UNAUTHORIZED, "TOKEN_EXPIRED", "JWT token has expired");
            return;
        } catch (MalformedJwtException | SignatureException e) {
            writeErrorResponse(response, HttpStatus.UNAUTHORIZED, "INVALID_TOKEN", "JWT token is invalid");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void authenticateUser(HttpServletRequest request, String token) {
        String email = jwtService.extractEmail(token);
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            var userDetails = userDetailsService.loadUserByUsername(email);
            if (jwtService.isTokenValid(token, userDetails.getUsername())) {
                var authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
    }

    private void authenticateApiClient(HttpServletRequest request, String token) {
        String clientId = jwtService.extractEmail(token); // subject = clientId
        if (clientId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            boolean exists = apiClientRepository.findByClientId(clientId)
                    .map(c -> c.isActive())
                    .orElse(false);

            if (exists && jwtService.isTokenValid(token, clientId)) {
                var authorities = List.of(new SimpleGrantedAuthority("ROLE_API_CLIENT"));
                var authToken = new UsernamePasswordAuthenticationToken(clientId, null, authorities);
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
    }

    private void writeErrorResponse(HttpServletResponse response, HttpStatus status, String code, String message) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), Map.of(
                "status", status.value(),
                "code", code,
                "message", message,
                "timestamp", Instant.now().toString()
        ));
    }
}
