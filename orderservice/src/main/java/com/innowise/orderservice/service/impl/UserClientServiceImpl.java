package com.innowise.orderservice.service.impl;

import com.innowise.orderservice.communication.UserClient;
import com.innowise.orderservice.dto.user.UserInfo;
import com.innowise.orderservice.exception.UserNotFoundException;
import com.innowise.orderservice.service.UserClientService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserClientServiceImpl implements UserClientService {

    private final UserClient userClient;

    private static final String USER_SERVICE = "userServiceCircuit";

    @CircuitBreaker(name = USER_SERVICE, fallbackMethod = "userNotFoundByEmailFallback")
    public UserInfo findUserByEmail(String email) {
        try {
            return userClient.findByEmail(email);
        } catch (HttpClientErrorException e) {
            throw new UserNotFoundException();
        }
    }

    @CircuitBreaker(name = USER_SERVICE, fallbackMethod = "userNotFoundByIdFallback")
    public UserInfo findUserById(Long id) {
        try {
            return userClient.findById(id);
        } catch (HttpClientErrorException e) {
            throw new UserNotFoundException();
        }
    }

    @CircuitBreaker(name = USER_SERVICE, fallbackMethod = "userNotFoundBySelfIdFallback")
    public UserInfo findUserBySelfId() {
        try {
            return userClient.findBySelfId();
        } catch (HttpClientErrorException e) {
            throw new UserNotFoundException();
        }
    }

    @CircuitBreaker(name = USER_SERVICE, fallbackMethod = "userListFallback")
    public List<UserInfo> findAllUsersById(List<Long> ids) {
        try {
            return userClient.findAllById(ids);
        } catch (HttpClientErrorException e) {
            throw new UserNotFoundException();
        }
    }

    private UserInfo userNotFoundByEmailFallback(String email, Throwable t) {
        if (t instanceof UserNotFoundException) {
            throw (UserNotFoundException) t;
        }
        throw new UserNotFoundException("User service unavailable");
    }

    private UserInfo userNotFoundBySelfIdFallback(Throwable t) {
        if (t instanceof UserNotFoundException) {
            throw (UserNotFoundException) t;
        }
        throw new UserNotFoundException("User service unavailable");
    }

    private UserInfo userNotFoundByIdFallback(Long id, Throwable t) {
        if (t instanceof UserNotFoundException) {
            throw (UserNotFoundException) t;
        }
        throw new UserNotFoundException("User service unavailable");
    }

    private List<UserInfo> userListFallback(List<Long> ids, Throwable t) {
        if (t instanceof UserNotFoundException) {
            throw (UserNotFoundException) t;
        }
        throw new UserNotFoundException("User service unavailable");
    }

}
