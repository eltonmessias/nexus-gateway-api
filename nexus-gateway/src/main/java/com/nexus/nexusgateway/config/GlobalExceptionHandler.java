package com.nexus.nexusgateway.config;

import com.nexus.nexuscommons.exception.ClientNotFoundException;
import com.nexus.nexuscommons.exception.FlagNotFoundException;
import com.nexus.nexuscommons.exception.JobNotFoundException;
import com.nexus.nexuscommons.exception.NexusException;
import com.nexus.nexuscommons.exception.RateLimitException;
import com.nexus.nexusiam.domain.exception.IamException;
import com.nexus.nexusiam.domain.exception.EmailAlreadyExistsException;
import com.nexus.nexusiam.domain.exception.InvalidCredentialsException;
import com.nexus.nexusiam.domain.exception.OrganizationNotFoundException;
import com.nexus.nexusiam.domain.exception.ProjectNotFoundException;
import com.nexus.nexusiam.domain.exception.SlugAlreadyExistsException;
import com.nexus.nexusiam.domain.exception.TeamNotFoundException;
import com.nexus.nexusiam.domain.exception.TokenExpiredException;
import com.nexus.nexusiam.domain.exception.UserNotFoundException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({FlagNotFoundException.class, JobNotFoundException.class, ClientNotFoundException.class})
    public ResponseEntity<Map<String, Object>> handleNotFound(NexusException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getCode(), ex.getMessage());
    }

    @ExceptionHandler({OrganizationNotFoundException.class, UserNotFoundException.class,
            TeamNotFoundException.class, ProjectNotFoundException.class})
    public ResponseEntity<Map<String, Object>> handleIamNotFound(IamException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getCode(), ex.getMessage());
    }

    @ExceptionHandler({EmailAlreadyExistsException.class, SlugAlreadyExistsException.class})
    public ResponseEntity<Map<String, Object>> handleIamConflict(IamException ex) {
        return buildResponse(HttpStatus.CONFLICT, ex.getCode(), ex.getMessage());
    }

    @ExceptionHandler({InvalidCredentialsException.class, BadCredentialsException.class})
    public ResponseEntity<Map<String, Object>> handleInvalidCredentials(Exception ex) {
        return buildResponse(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS", "Invalid email or password");
    }

    @ExceptionHandler({TokenExpiredException.class, ExpiredJwtException.class})
    public ResponseEntity<Map<String, Object>> handleTokenExpired(Exception ex) {
        return buildResponse(HttpStatus.UNAUTHORIZED, "TOKEN_EXPIRED", "JWT token has expired");
    }

    @ExceptionHandler({MalformedJwtException.class, SignatureException.class})
    public ResponseEntity<Map<String, Object>> handleInvalidToken(Exception ex) {
        return buildResponse(HttpStatus.UNAUTHORIZED, "INVALID_TOKEN", "JWT token is invalid");
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<Map<String, Object>> handleDisabled(DisabledException ex) {
        return buildResponse(HttpStatus.FORBIDDEN, "ACCOUNT_DISABLED", "Account is disabled");
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAuthorizationDenied(AuthorizationDeniedException ex) {
        return buildResponse(HttpStatus.FORBIDDEN, "ACCESS_DENIED", "You do not have permission to perform this action");
    }

    @ExceptionHandler(RateLimitException.class)
    public ResponseEntity<Map<String, Object>> handleRateLimit(RateLimitException ex) {
        return buildResponse(HttpStatus.TOO_MANY_REQUESTS, ex.getCode(), ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(java.util.stream.Collectors.joining("; "));
        return buildResponse(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", message);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNoResource(NoResourceFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, "NOT_FOUND", ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "An unexpected error occurred");
    }

    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String code, String message) {
        return ResponseEntity.status(status).body(Map.of(
                "status", status.value(),
                "code", code,
                "message", message,
                "timestamp", Instant.now().toString()
        ));
    }
}
