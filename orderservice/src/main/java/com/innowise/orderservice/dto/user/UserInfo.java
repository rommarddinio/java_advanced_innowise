package com.innowise.orderservice.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object representing basic user information.
 * <p>
 * Used for transferring user data between services or layers without exposing internal entities.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {

    /**
     * Unique identifier of the user.
     */
    private Long id;

    /**
     * First name of the user.
     */
    private String name;

    /**
     * Last name (surname) of the user.
     */
    private String surname;

    /**
     * Email address of the user.
     */
    private String email;

}