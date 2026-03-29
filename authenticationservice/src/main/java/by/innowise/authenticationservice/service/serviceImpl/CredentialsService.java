package by.innowise.authenticationservice.service;

import by.innowise.authenticationservice.details.MyUserDetails;
import by.innowise.authenticationservice.entity.Credentials;
import by.innowise.authenticationservice.repository.CredentialsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class CredentialsService implements UserDetailsService {

    private final CredentialsRepository credentialsRepository;

    @Override
    public MyUserDetails loadUserByUsername(String login) throws UsernameNotFoundException {

        Credentials credentials = credentialsRepository.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException(String
                        .format("User with login %s is not found", login)));

        return MyUserDetails.builder()
                .userId(credentials.getUserId())
                .username(credentials.getLogin())
                .role(credentials.getRole().toString())
                .password(credentials.getPassword())
                .build();
    }

    public void saveCredentials(Credentials credentials) {
        credentialsRepository.save(credentials);
    }

}
