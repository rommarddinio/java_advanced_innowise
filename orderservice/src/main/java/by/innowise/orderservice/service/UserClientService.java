package by.innowise.orderservice.service;

import by.innowise.orderservice.dto.user.UserInfo;

import java.util.List;

public interface UserClientService {

    UserInfo findUserByEmail(String email);

    UserInfo findUserById(Long id);

    UserInfo findUserBySelfId();

    List<UserInfo> findAllUsersById(List<Long> ids);

}
