package by.innowise.authenticationservice.service;

import by.innowise.authenticationservice.details.MyUserDetails;
import by.innowise.authenticationservice.dto.GeneralRequest;
import by.innowise.authenticationservice.dto.GeneralResponse;
import by.innowise.authenticationservice.dto.LoginResponse;
import by.innowise.authenticationservice.entity.Credentials;
import by.innowise.authenticationservice.exception.EmptyTokenException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private CredentialsService credentialsService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private TokenService tokenService;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private GeneralRequest generalRequest;

    @BeforeEach
    void setUp() {
        generalRequest = new GeneralRequest();
        generalRequest.setLogin("testuser");
        generalRequest.setPassword("password");
        generalRequest.setUserId(1L);
        generalRequest.setRole("ROLE_USER");
    }

    @Test
    void login_ShouldReturnLoginResponse_WhenCredentialsAreValid() {
        MyUserDetails userDetails = MyUserDetails.builder()
                .userId(1L)
                .username("testuser")
                .role("ROLE_USER")
                .build();

        when(credentialsService.loadUserByUsername("testuser")).thenReturn(userDetails);
        when(tokenService.generateAccessToken(1L, "ROLE_USER")).thenReturn("access-token");
        when(tokenService.generateRefreshToken(1L, "ROLE_USER")).thenReturn("refresh-token");

        LoginResponse response = authService.login(generalRequest);

        assertEquals("access-token", response.getAccessToken());
        assertEquals("refresh-token", response.getRefreshToken());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void register_ShouldReturnGeneralResponse_WithAccessToken() {
        when(passwordEncoder.encode("password")).thenReturn("hashed-password");
        when(tokenService.generateAccessToken(1L, "ROLE_USER")).thenReturn("access-token");

        GeneralResponse response = authService.register(generalRequest);

        assertEquals("access-token", response.getToken());
        verify(credentialsService).saveCredentials(any(Credentials.class));
    }

    @Test
    void validate_ShouldThrowEmptyTokenException_WhenHeaderIsNull() {
        assertThrows(EmptyTokenException.class, () -> authService.validate(null));
    }

    @Test
    void validate_ShouldThrowEmptyTokenException_WhenHeaderInvalid() {
        assertThrows(EmptyTokenException.class, () -> authService.validate("InvalidHeader"));
    }

    @Test
    void validate_ShouldCallGetClaimsFromToken_WhenHeaderValid() {
        String token = "valid-token";
        when(tokenService.getClaimsFromToken(token)).thenReturn(null);

        authService.validate("Bearer " + token);

        verify(tokenService).getClaimsFromToken(token);
    }

    @Test
    void refresh_ShouldReturnNewAccessToken_WhenHeaderValid() {
        String token = "valid-token";
        when(tokenService.getUserId(token)).thenReturn(1L);
        when(tokenService.getRole(token)).thenReturn("ROLE_USER");
        when(tokenService.generateAccessToken(1L, "ROLE_USER")).thenReturn("new-access-token");

        GeneralResponse response = authService.refresh("Bearer " + token);

        assertEquals("new-access-token", response.getToken());
        verify(tokenService).generateAccessToken(1L, "ROLE_USER");
    }

    @Test
    void refresh_ShouldThrowEmptyTokenException_WhenHeaderInvalid() {
        assertThrows(EmptyTokenException.class, () -> authService.refresh(null));
    }
}
