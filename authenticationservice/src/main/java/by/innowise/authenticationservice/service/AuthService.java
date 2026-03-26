package by.innowise.authenticationservice.service;

import by.innowise.authenticationservice.details.MyUserDetails;
import by.innowise.authenticationservice.dto.GeneralRequest;
import by.innowise.authenticationservice.dto.GeneralResponse;
import by.innowise.authenticationservice.dto.LoginResponse;
import by.innowise.authenticationservice.dto.TokenPayload;
import by.innowise.authenticationservice.entity.Credentials;
import by.innowise.authenticationservice.enums.TokenType;
import by.innowise.authenticationservice.exception.EmptyTokenException;
import by.innowise.authenticationservice.enums.Role;
import by.innowise.authenticationservice.exception.InvalidTokenTypeException;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final CredentialsService credentialsService;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final BCryptPasswordEncoder passwordEncoder;

    public LoginResponse login(GeneralRequest generalRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                generalRequest.getLogin(), generalRequest.getPassword()));
        MyUserDetails userDetails = credentialsService.loadUserByUsername(generalRequest.getLogin());

        return new LoginResponse(tokenService.generateAccessToken(userDetails.getUserId(), userDetails.getRole()),
                tokenService.generateRefreshToken(userDetails.getUserId(), userDetails.getRole()));
    }

    public GeneralResponse register(GeneralRequest generalRequest) {
        Credentials credentials = new Credentials();
        credentials.setUserId(generalRequest.getUserId());
        credentials.setRole((generalRequest.getRole() == null) ? Role.ROLE_USER :
                Role.valueOf(generalRequest.getRole()));
        credentials.setLogin(generalRequest.getLogin());
        credentials.setPassword(passwordEncoder.encode(generalRequest.getPassword()));

        credentialsService.saveCredentials(credentials);

        return new GeneralResponse(tokenService.generateAccessToken(generalRequest.getUserId(), generalRequest.getRole()));
    }

    public TokenPayload validate(String header) {
        if (header == null || !header.startsWith("Bearer ")) {
            throw new EmptyTokenException();
        }
        String token = header.substring(7);

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
        validate(header);
        String token = header.substring(7);
        if (!tokenService.getTokenType(token).equals(TokenType.REFRESH.name())) {
            throw new InvalidTokenTypeException();
        }

        String newAccessToken = tokenService.generateAccessToken(
                tokenService.getUserId(token), tokenService.getRole(token));

        return new GeneralResponse(newAccessToken);
    }

}
