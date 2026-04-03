package by.innowise.orderservice.communication;

import by.innowise.orderservice.dto.user.UserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserClient {

    private final RestClient restClient;

    public UserInfo findByEmail(String email) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search")
                        .queryParam("email", email)
                        .build())
                .retrieve()
                .body(UserInfo.class);
    }

    public UserInfo findById(Long id) {
        return restClient.get()
                .uri("/{id}", id)
                .retrieve()
                .body(UserInfo.class);
    }

    public UserInfo findBySelfId() {
        return restClient.get()
                .uri("/me")
                .retrieve()
                .body(UserInfo.class);
    }


    public List<UserInfo> findAllById(List<Long> ids) {
        return restClient.post()
                .uri("/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .body(ids)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

}
