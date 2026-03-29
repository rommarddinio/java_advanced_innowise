package by.innowise.authenticationservice.service;

import by.innowise.authenticationservice.dto.*;

public interface AuthService {

    LoginResponse login(LoginRequest request);

    GeneralResponse register(RegisterRequest request);

    TokenPayload validate(String header);

    GeneralResponse refresh(String header);

}
