package by.innowise.authenticationservice.controller;

import by.innowise.authenticationservice.dto.GeneralRequest;
import by.innowise.authenticationservice.dto.GeneralResponse;
import by.innowise.authenticationservice.dto.LoginResponse;
import by.innowise.authenticationservice.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody GeneralRequest request) {
        return new ResponseEntity<>(authService.login(request), HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<GeneralResponse> register(@RequestBody GeneralRequest request) {
        return new ResponseEntity<>(authService.register(request), HttpStatus.OK);
    }

    @PostMapping("/validate")
    public ResponseEntity<String> validate(@RequestHeader("Authorization") String header) {
        authService.validate(header);
        return new ResponseEntity<>("Token is valid", HttpStatus.OK);
    }

    @PostMapping("/refresh")
    public ResponseEntity<GeneralResponse> refresh(@RequestHeader("Authorization") String header) {
        return new ResponseEntity<>(authService.refresh(header), HttpStatus.OK);
    }

}
