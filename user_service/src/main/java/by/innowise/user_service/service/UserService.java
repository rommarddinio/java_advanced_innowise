package by.innowise.user_service.service;

import by.innowise.user_service.dto.UserDto;
import org.springframework.data.domain.Page;

public interface UserService {

    UserDto createUser(UserDto userDto);

    UserDto updateUser(Long id, UserDto userDto);

    UserDto findById(Long id);

    void activateUser(Long id);

    void deactivateUser(Long id);

    Page<UserDto> getUsers(int page, int size, String surname, String name);

    void deleteById(Long id);

}
