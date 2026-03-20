package by.innowise.user_service.service.serviceImpl;

import by.innowise.user_service.dto.UserDto;
import by.innowise.user_service.dto.mapping.UserMapper;
import by.innowise.user_service.entity.User;
import by.innowise.user_service.exception.UserNotFoundException;
import by.innowise.user_service.repository.PaymentCardRepository;
import by.innowise.user_service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PaymentCardRepository paymentCardRepository;

    @Mock
    private UserMapper userMapper;

    private UserDto userDto;
    private User user;

    @BeforeEach
    void setUp() {

        userDto = new UserDto();
        user = new User();

    }

    @Test
    void createUser_ShouldReturnUserDto_WhenSuccessful() {

        userDto.setEmail("test@gmail.com");
        user.setEmail("test@gmail.com");

        when(userMapper.toUser(userDto)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toUserDto(user)).thenReturn(userDto);

        UserDto result = userService.createUser(userDto);

        assertNotNull(result);
        assertEquals("test@gmail.com", result.getEmail());

        verify(userMapper).toUser(userDto);
        verify(userRepository).save(user);
        verify(userMapper).toUserDto(user);

    }

    @Test
    void createUser_ShouldThrowException_WhenEmailNotUnique() {

        when(userMapper.toUser(userDto)).thenReturn(user);
        when(userRepository.save(user)).thenThrow(DataIntegrityViolationException.class);

        assertThrows(DataIntegrityViolationException.class, () -> userService.createUser(userDto));

        verify(userMapper).toUser(userDto);
        verify(userRepository).save(user);

    }

    @Test
    void findById_ShouldReturnUser_WhenSuccessful() {

        user.setName("Roman");
        userDto.setName("Roman");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toUserDto(user)).thenReturn(userDto);
        UserDto result = userService.findById(1L);

        assertNotNull(result);
        assertEquals("Roman", result.getName());

        verify(userRepository).findById(1L);
        verify(userMapper).toUserDto(user);

    }

    @Test
    void findById_ShouldThrowException_WhenNotFound() {

        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userService.findById(99L));

        verify(userRepository).findById(99L);
        verifyNoInteractions(userMapper);

    }

    @Test
    void updateUser_ShouldReturnUserDto__WhenSuccessful() {

        userDto.setName("Roman");
        userDto.setId(1L);
        user.setName("Roma");
        user.setId(1L);

        UserDto updatedUserDto = new UserDto();
        updatedUserDto.setId(1L);
        updatedUserDto.setName("Roman");
        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setName("Roman");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(updatedUser);
        when(userMapper.toUserDto(updatedUser)).thenReturn(updatedUserDto);

        UserDto result = userService.updateUser(1L, userDto);

        assertNotNull(result);
        assertEquals(updatedUser.getName(), result.getName());
        assertEquals(1L, result.getId());

        verify(userRepository).findById(1L);
        verify(userRepository).save(user);
        verify(userMapper).toUserDto(updatedUser);

    }

    @Test
    void updateUser_ShouldThrowException_WhenEmailNotUnique() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenThrow(DataIntegrityViolationException.class);

        assertThrows(DataIntegrityViolationException.class, () -> userService
                .updateUser(1L,userDto));

        verify(userRepository).findById(1L);
        verify(userRepository).save(user);
        verifyNoInteractions(userMapper);

    }

    @Test
    void updateUser_ShouldThrowException_WhenNotFound() {

        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.updateUser(99L, userDto));

        verify(userRepository).findById(99L);
        verifyNoInteractions(userMapper);

    }

    @Test
    void getUsers_ShouldReturnPageWithoutFilters() {

        int page = 0;
        int size = 10;

        Page<User> userPage = new PageImpl<>(List.of(user),
                PageRequest.of(page, size), 1);

        when(userRepository.findAll(any(Specification.class), any(PageRequest.class)))
                .thenReturn(userPage);
        when(userMapper.toUserDto(user)).thenReturn(userDto);

        Page<UserDto> result = userService
                .getUsers(page, size, null, null);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());

        verify(userRepository).findAll(any(Specification.class), any(PageRequest.class));
        verify(userMapper).toUserDto(user);

    }

    @Test
    void getUsers_ShouldReturnEmptyPage() {

        int page = 0;
        int size = 10;

        Page<User> userPage = new PageImpl<>(List.of(),
                PageRequest.of(page, size), 0);

        when(userRepository.findAll(any(Specification.class), any(PageRequest.class)))
                .thenReturn(userPage);

        Page<UserDto> result =
                userService.getUsers(page, size, null, null);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(userRepository).findAll(any(Specification.class), any(PageRequest.class));
        verifyNoInteractions(userMapper);

    }

    @Test
    void getUsers_ShouldReturnPageWithFilters() {

        int page = 0;
        int size = 10;

        userDto.setName("Roman");
        userDto.setSurname("Sidorchuk");
        user.setName("Roman");
        user.setSurname("Sidorchuk");

        Page<User> userPage = new PageImpl<>(List.of(user),
                PageRequest.of(page, size), 1);

        when(userRepository.findAll(any(Specification.class), any(PageRequest.class)))
                .thenReturn(userPage);
        when(userMapper.toUserDto(user))
                .thenReturn(userDto);

        Page<UserDto> result = userService
                .getUsers(page, size, "Roman", "Sidorchuk");

        assertNotNull(result);
        assertEquals("Roman", result.getContent().getFirst().getName());
        assertEquals("Sidorchuk", result.getContent().getFirst().getSurname());
        assertEquals(1, result.getTotalElements());

        verify(userRepository).findAll(any(Specification.class), any(PageRequest.class));
        verify(userMapper).toUserDto(user);

    }

    @Test
    void activateUser_ShouldReturnUpdatedRow_WhenSuccessful() {

        when(userRepository.setActiveUser(1L, true)).thenReturn(1);

        userService.activateUser(1L);

        verify(userRepository).setActiveUser(1L, true);

    }

    @Test
    void activateUser_ShouldThrowException_WhenNotFound() {

        when(userRepository.setActiveUser(99L, true)).thenReturn(0);

        assertThrows(UserNotFoundException.class,
                () -> userService.activateUser(99L));

        verify(userRepository).setActiveUser(99L, true);

    }

    @Test
    void deactivateUser_ShouldReturnUpdatedRow_WhenSuccessful() {

        when(userRepository.setActiveUser(1L, false)).thenReturn(1);

        userService.deactivateUser(1L);

        verify(userRepository).setActiveUser(1L, false);
        verify(paymentCardRepository).deactivateByUserId(1L);

    }

    @Test
    void deactivateUser_ShouldThrowException_WhenNotFound() {

        when(userRepository.setActiveUser(99L, false)).thenReturn(0);

        assertThrows(UserNotFoundException.class,
                () -> userService.deactivateUser(99L));

        verify(userRepository).setActiveUser(99L, false);

    }

    @Test
    void deleteById_ShouldDeleteUser() {

        userService.deleteById(1L);

        verify(userRepository).deleteById(1L);

    }

}