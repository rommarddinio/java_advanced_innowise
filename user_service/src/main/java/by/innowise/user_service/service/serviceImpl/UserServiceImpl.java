package by.innowise.user_service.service.serviceImpl;

import by.innowise.user_service.dto.UserDto;
import by.innowise.user_service.dto.mapping.UserMapper;
import by.innowise.user_service.entity.User;
import by.innowise.user_service.exception.UserNotFoundException;
import by.innowise.user_service.repository.UserRepository;
import by.innowise.user_service.service.UserService;
import by.innowise.user_service.specification.UserSpecifications;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto createUser(UserDto userDto) {
        userDto.setActive(true);
        return userMapper.toUserDto(userRepository.save(userMapper.toUser(userDto)));
    }

    @CacheEvict(
            value = "user",
            key = "#id"
    )
    @Transactional
    @Override
    public UserDto updateUser(Long id, UserDto userDto) {
        User user = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);

        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        user.setSurname(userDto.getSurname());
        user.setBirthDate(userDto.getBirthDate());

        return userMapper.toUserDto(userRepository.save(user));
    }

    @Cacheable(
            value = "user",
            key = "#id"
    )
    @Override
    public UserDto findById(Long id) {
        return userRepository.findById(id).map(userMapper::toUserDto)
                .orElseThrow(UserNotFoundException::new);
    }

    @CacheEvict(
            value = "user",
            key = "#id"
    )
    @Transactional
    @Override
    public void activateUser(Long id) {
        int rows = userRepository.setActiveUser(id, true);
        if (rows == 0) throw new UserNotFoundException();
    }

    @CacheEvict(
            value = "user",
            key = "#id"
    )
    @Transactional
    @Override
    public void deactivateUser(Long id) {
        int rows = userRepository.setActiveUser(id, false);
        if (rows == 0) throw new UserNotFoundException();
    }

    @Override
    public Page<UserDto> getUsers(int page, int size, String name, String surname) {
        Specification<User> specification = Specification.where(UserSpecifications.hasName(name))
                .and(UserSpecifications.hasSurname(surname));
        return userRepository.findAll(specification, PageRequest.of(page, size))
                .map(userMapper::toUserDto);
    }

    @CacheEvict(
            value = "user",
            key = "#id"
    )
    @Transactional
    @Override
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }
}
