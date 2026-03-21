package by.innowise.authenticationservice.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class TokenService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access_lifetime}")
    private Duration accessLifetime;

    @Value("${jwt.refresh_lifetime}")
    private Duration refreshLifetime;

    public String generateAccessToken(Long userId, String role) {
        return getToken(userId, role, accessLifetime);
    }

    public String generateRefreshToken(Long userId, String role) {
        return getToken(userId, role, refreshLifetime);
    }

    private String getToken(Long userId, String role, Duration refreshLifetime) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("role", role);

        Date issuedDate = new Date();
        Date expiriedDate = new Date(issuedDate.getTime() + refreshLifetime.toMillis());

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(issuedDate)
                .setExpiration(expiriedDate)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }

    public Long getUserId(String token) {
        return getClaimsFromToken(token).get("userId", Long.class);
    }

    public String getRole(String token) {
        return getClaimsFromToken(token).get("role", String.class);
    }

}
