package by.innowise.authenticationservice.service;

import by.innowise.authenticationservice.details.MyUserDetails;
import by.innowise.authenticationservice.entity.Credentials;
import by.innowise.authenticationservice.repository.CredentialsRepository;
import by.innowise.authenticationservice.enums.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CredentialsServiceTest {

    @Mock
    private CredentialsRepository credentialsRepository;

    @InjectMocks
    private CredentialsService credentialsService;

    @Test
    void loadUserByUsername_ShouldReturnUserDetails_WhenUserExists() {
        Credentials credentials = new Credentials();
        credentials.setUserId(1L);
        credentials.setLogin("testuser");
        credentials.setPassword("hashedpassword");
        credentials.setRole(Role.ROLE_USER);

        when(credentialsRepository.findByLogin("testuser")).thenReturn(Optional.of(credentials));

        MyUserDetails userDetails = credentialsService.loadUserByUsername("testuser");

        assertNotNull(userDetails);
        assertEquals(1L, userDetails.getUserId());
        assertEquals("testuser", userDetails.getUsername());
        assertEquals("ROLE_USER", userDetails.getRole());
        assertEquals("hashedpassword", userDetails.getPassword());
    }

    @Test
    void loadUserByUsername_ShouldThrowException_WhenUserNotFound() {
        when(credentialsRepository.findByLogin("unknown")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () ->
                credentialsService.loadUserByUsername("unknown"));
    }

    @Test
    void saveCredentials_ShouldCallRepositorySave() {
        Credentials credentials = new Credentials();
        credentialsService.saveCredentials(credentials);

        verify(credentialsRepository, times(1)).save(credentials);
    }
}
