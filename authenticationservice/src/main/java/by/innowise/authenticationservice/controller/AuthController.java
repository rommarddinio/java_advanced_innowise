package by.innowise.authenticationservice.controller;

import by.innowise.authenticationservice.dto.*;
import org.springframework.http.ResponseEntity;

/**
 * Controller for authentication operations such as login, registration,
 * token validation and refresh token.
 */
public interface AuthController {

    /**
     * Authenticates user and returns access and refresh tokens.
     *
     * @param request login request containing credentials
     * @return JWT access and refresh tokens
     */
    ResponseEntity<LoginResponse> login(LoginRequest request);

    /**
     * Registers a new user.
     *
     * @param request registration data
     * @return JWT access token
     */
    ResponseEntity<GeneralResponse> register(RegisterRequest request);

    /**
     * Validate token.
     *
     * @param header Authorization header with token to validate
     * @return token payload
     */
    ResponseEntity<TokenPayload> validate(String header);

    /**
     * Refreshes access token using refresh token.
     *
     * @param header Authorization header with refresh token
     * @return new access token
     */
    ResponseEntity<GeneralResponse> refresh(String header);
}
