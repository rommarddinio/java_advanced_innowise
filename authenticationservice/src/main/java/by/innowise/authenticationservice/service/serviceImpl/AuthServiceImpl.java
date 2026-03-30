package by.innowise.authenticationservice.service.serviceImpl;

import by.innowise.authenticationservice.details.MyUserDetails;
import by.innowise.authenticationservice.dto.*;
import by.innowise.authenticationservice.entity.Credentials;
import by.innowise.authenticationservice.enums.TokenType;
import by.innowise.authenticationservice.exception.EmptyTokenException;
import by.innowise.authenticationservice.enums.Role;
import by.innowise.authenticationservice.exception.InvalidTokenTypeException;
import by.innowise.authenticationservice.service.AuthService;
import by.innowise.authenticationservice.service.TokenService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final CredentialsService credentialsService;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final BCryptPasswordEncoder passwordEncoder;

    public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getLogin(), request.getPassword()));
        MyUserDetails userDetails = credentialsService.loadUserByUsername(request.getLogin());

        return new LoginResponse(tokenService.generateAccessToken(userDetails.getUserId(), userDetails.getRole()),
                tokenService.generateRefreshToken(userDetails.getUserId(), userDetails.getRole()));
    }

    public GeneralResponse register(RegisterRequest request) {
        Credentials credentials = new Credentials();
        credentials.setUserId(request.getUserId());
        credentials.setRole(Role.ROLE_USER);
        credentials.setLogin(request.getLogin());
        credentials.setPassword(passwordEncoder.encode(request.getPassword()));

        credentialsService.saveCredentials(credentials);

        return new GeneralResponse(tokenService.generateAccessToken(request.getUserId(),
                Role.ROLE_USER.name()));
    }

    public TokenPayload validate(String header) {
        String token = extractToken(header);

        Claims claims = tokenService.getClaimsFromToken(token);

        return TokenPayload.builder()
                .userId(claims.get("userId", Long.class))
                .role(claims.get("role",String.class))
                .tokenType(claims.get("tokenType", String.class))
                .expiration(claims.getExpiration())
                .issuedAt(claims.getIssuedAt())
                .build();
    }

    public GeneralResponse refresh(String header) {

        String token = extractToken(header);

        if (!tokenService.getTokenType(token).equals(TokenType.REFRESH.name())) {
            throw new InvalidTokenTypeException();
        }

        String newAccessToken = tokenService.generateAccessToken(
                tokenService.getUserId(token), tokenService.getRole(token));

        return new GeneralResponse(newAccessToken);
    }

    private String extractToken(String header) {
        if (header == null || !header.startsWith("Bearer ")) {
            throw new EmptyTokenException();
        }
        return header.substring(7);
    }

}
