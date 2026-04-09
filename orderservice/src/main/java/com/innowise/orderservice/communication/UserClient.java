package com.innowise.orderservice.communication;

import com.innowise.orderservice.dto.user.UserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

/**
 * Client for interacting with the external User Service via REST API.
 * <p>
 * Provides methods to fetch user information by email, ID, current user,
 * or batch of IDs. Encapsulates HTTP calls and response parsing.
 */
@Component
@RequiredArgsConstructor
public class UserClient {

    private final RestClient restClient;

    /**
     * Retrieves user information by email.
     *
     * @param email user's email address
     * @return {@link UserInfo} object corresponding to the email
     */
    public UserInfo findByEmail(String email) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search")
                        .queryParam("email", email)
                        .build())
                .retrieve()
                .body(UserInfo.class);
    }

    /**
     * Retrieves user information by user ID.
     *
     * @param id unique identifier of the user
     * @return {@link UserInfo} object corresponding to the user ID
     */
    public UserInfo findById(Long id) {
        return restClient.get()
                .uri("/{id}", id)
                .retrieve()
                .body(UserInfo.class);
    }

    /**
     * Retrieves information about the currently authenticated user.
     *
     * @return {@link UserInfo} of the authenticated user
     */
    public UserInfo findBySelfId() {
        return restClient.get()
                .uri("/me")
                .retrieve()
                .body(UserInfo.class);
    }

    /**
     * Retrieves information about multiple users by their IDs.
     *
     * @param ids list of user IDs
     * @return list of {@link UserInfo} objects
     */
    public List<UserInfo> findAllById(List<Long> ids) {
        return restClient.post()
                .uri("/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .body(ids)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

}