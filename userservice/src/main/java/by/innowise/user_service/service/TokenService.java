package by.innowise.user_service.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
public class TokenService {

    @Value("${jwt.secret}")
    private String secret;

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

    public String getTokenType(String token) {
        return getClaimsFromToken(token).get("tokenType", String.class);
    }

}

