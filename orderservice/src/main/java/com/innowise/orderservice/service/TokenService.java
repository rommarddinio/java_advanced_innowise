package com.innowise.orderservice.service;

import io.jsonwebtoken.Claims;

/**
 * Service for working with JWT tokens.
 * <p>
 * Provides methods for extracting and parsing token data such as claims,
 * user identification, roles, and token metadata.
 */
public interface TokenService {

    /**
     * Extracts all claims from the provided JWT token.
     *
     * @param token JWT token string
     * @return parsed {@link Claims} object containing token data
     */
    Claims getClaimsFromToken(String token);

    /**
     * Extracts user ID from the JWT token.
     *
     * @param token JWT token string
     * @return user ID contained in the token
     */
    Long getUserId(String token);

    /**
     * Extracts or formats a role value.
     * <p>
     * Implementation may normalize role format (e.g., add prefix like "ROLE_").
     *
     * @param role raw role value
     * @return processed role string
     */
    String getRole(String role);

    /**
     * Extracts token type (e.g., access or refresh) from the JWT token.
     *
     * @param token JWT token string
     * @return token type
     */
    String getTokenType(String token);

    /**
     * Retrieves JWT token from the current HTTP request.
     * <p>
     * Typically extracts token from the Authorization header.
     *
     * @return JWT token string
     */
    String getTokenFromRequest();

}
