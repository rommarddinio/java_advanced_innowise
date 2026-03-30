package by.innowise.authenticationservice.dto;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TokenPayload {
    private String role;
    private String tokenType;
    private Long userId;
    private Date issuedAt;
    private Date expiration;
}
