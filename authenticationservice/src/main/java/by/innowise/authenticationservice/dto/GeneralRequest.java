package by.innowise.authenticationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GeneralRequest {
    private Long userId;
    private String login;
    private String password;
    private String role;
}
