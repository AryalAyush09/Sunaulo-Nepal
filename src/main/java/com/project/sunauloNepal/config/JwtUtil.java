package com.project.sunauloNepal.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.project.sunauloNepal.ENUM.Role;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private Key key;

    @Value("${JWT_SECRET:mysupersecuresecretkeyforjwtgeneration123!}")
    private String secret;

    @Value("${jwt.reset.expiration-minutes:15}")
    private long resetTokenExpirationMinutes;

    @Value("${jwt.token.expiration-days:20}")
    private long tokenExpirationDays;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }
    
//     Generate JWT for regular login :: valid 1 hour
    public String generateToken(Long userId, Role role) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId))  // use userId as subject
                .claim("role", role.name())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1 hour
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
    
     // Generate short lived token for password reset or sensitive operations
    public String generateResetToken(String email, Role role) {
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role.name())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + tokenExpirationDays * 24 * 60 * 60 * 1000))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
    
//      Extract email from token  
    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }

//      Extract role from token
    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

//      Check if token is expired
    public boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

//      Validate token matches the email and is not expired  
    public boolean validateToken(String token, String email) {
        return extractEmail(token).equals(email) && !isTokenExpired(token);
    }
 
//      Extract all claims  
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
