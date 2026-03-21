package by.innowise.authenticationservice.entity;

import by.innowise.authenticationservice.role.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Table(name = "credentials")
@Entity
public class Credentials {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    private String login;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

}
