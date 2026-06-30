package com.nexus.nexusiam.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtProperties jwtProperties;

    public String generateToken(String email, UUID userId, String organizationId, String role) {
        return Jwts.builder()
                .subject(email)
                .claims(Map.of(
                        "userId", userId.toString(),
                        "organizationId", organizationId,
                        "role", role,
                        "type", "access"
                ))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtProperties.getExpiration()))
                .signWith(getSignInKey())
                .compact();
    }

    public String generateClientToken(String clientId, java.util.UUID clientUUID, String organizationId, String projectId) {
        return Jwts.builder()
                .subject(clientId)
                .claims(Map.of(
                        "clientId", clientUUID.toString(),
                        "organizationId", organizationId,
                        "projectId", projectId,
                        "type", "client_access"
                ))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtProperties.getExpiration()))
                .signWith(getSignInKey())
                .compact();
    }

    public String generateRefreshToken(String email) {
        return Jwts.builder()
                .subject(email)
                .claims(Map.of("type", "refresh"))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtProperties.getRefreshExpiration()))
                .signWith(getSignInKey())
                .compact();
    }

    public String extractTokenType(String token) {
        return extractClaim(token, claims -> claims.get("type", String.class));
    }

    public boolean isRefreshToken(String token) {
        return "refresh".equals(extractTokenType(token));
    }

    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public UUID extractUserId(String token) {
        return UUID.fromString(extractClaim(token, claims -> claims.get("userId", String.class)));
    }

    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    public boolean isTokenValid(String token, String email) {
        return extractEmail(token).equals(email) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        return claimsResolver.apply(extractAllClaims(token));
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSignInKey() {
        return Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes());
    }
}
