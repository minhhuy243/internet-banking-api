package com.internetbanking.security;

import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Component
@PropertySource("classpath:application.yml")
public class JwtProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtProvider.class);

    @Value("${internet-banking.app.jwt-secret}")
    private String jwtSecret;

    @Value("${internet-banking.app.jwt-duration}")
    private Long jwtDuration;

    @Value("${internet-banking.app.jwt-refresh-duration}")
    private Long jwtRefreshDuration;

    public Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
    }

    public String getUsername(String token) {
        Claims claims = extractAllClaims(token);
        return claims.getSubject();
    }

    public LocalDateTime getExpirationDate(String token) {
        Claims claims = extractAllClaims(token);
        return claims.getExpiration().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public Boolean isTokenExpired(String token) {
        LocalDateTime expiration = getExpirationDate(token);
        return expiration.isBefore(LocalDateTime.now());
    }

    public String generateJwtToken(String subject) {
        Date issuedAt = new Date();
        Date expiration = new Date(issuedAt.getTime() + jwtDuration);
        return doGenerateJwtToken(subject, issuedAt, expiration);
    }

    public String generateJwtRefreshToken(String subject) {
        Date issuedAt = new Date();
        Date expiration = new Date(issuedAt.getTime() + jwtRefreshDuration);
        return doGenerateJwtToken(subject, issuedAt, expiration);
    }

    public String doGenerateJwtToken(String subject, Date issuedAt, Date expiration) {
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(issuedAt)
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public boolean validate(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token -> Message: {}", e);
        } catch (ExpiredJwtException e) {
            logger.error("Expired JWT token -> Message: {}", e);
        } catch (UnsupportedJwtException e) {
            logger.error("Unsupported JWT token -> Message: {}", e);
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty -> Message: {}", e);
        }
        return false;
    }
}
