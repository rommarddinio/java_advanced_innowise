package com.innowise.orderservice.service;

import com.innowise.orderservice.dto.user.UserInfo;

import java.util.List;

/**
 * Service for interacting with external User Service.
 * <p>
 * Provides methods to retrieve user information by different identifiers.
 * Typically used for communication between microservices.
 */
public interface UserClientService {

    /**
     * Retrieves user information by email.
     *
     * @param email user's email address
     * @return user information as {@link UserInfo}
     */
    UserInfo findUserByEmail(String email);

    /**
     * Retrieves user information by user ID.
     *
     * @param id unique user identifier
     * @return user information as {@link UserInfo}
     */
    UserInfo findUserById(Long id);

    /**
     * Retrieves information about the currently authenticated user.
     *
     * @return current user information as {@link UserInfo}
     */
    UserInfo findUserBySelfId();

    /**
     * Retrieves information about multiple users by their IDs.
     *
     * @param ids list of user identifiers
     * @return list of user information objects
     */
    List<UserInfo> findAllUsersById(List<Long> ids);

}