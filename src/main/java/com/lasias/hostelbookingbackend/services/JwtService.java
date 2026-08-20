package com.lasias.hostelbookingbackend.services;

import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JwtService {

    @Value("${app.jwt.secret}")
    private String secretKey;


    public Long extractUserId(String token){
        return Long.parseLong(Jwts.parserBuilder()
                .setSigningKey(secretKey.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject());
    }

    public List<GrantedAuthority> extractAuthorities(String token){
        String role = Jwts.parserBuilder()
                .setSigningKey(secretKey.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("role", String.class);

        if (role == null || role.isBlank()){
            return Collections.emptyList();
        }
        String authority = role.startsWith("ROLE_") ? role : "ROLE_" + role;

        return List.of(new SimpleGrantedAuthority(authority));
    }

    public String createBearerToken(String jwt){
        return "Bearer " + jwt;
    }

    public LocalDateTime extractIAT(String jwt) {
        return LocalDateTime.ofInstant(Jwts.parserBuilder().setSigningKey(secretKey.getBytes()).build().parseClaimsJws(jwt).getBody().getIssuedAt().toInstant(), java.time.ZoneId.systemDefault());
    }

    public String extractEmail(String jwt) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey.getBytes())
                .build()
                .parseClaimsJws(jwt)
                .getBody()
                .get("email", String.class);
    }
}
