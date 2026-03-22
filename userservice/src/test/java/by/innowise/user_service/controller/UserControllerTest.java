package by.innowise.user_service.controller;

import by.innowise.user_service.dto.MyUserDetails;
import by.innowise.user_service.dto.UserDto;
import by.innowise.user_service.entity.User;
import by.innowise.user_service.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = "JWT_SECRET=testsecret")
@WithMockUser(username = "1L", roles = "ADMIN")
class UserControllerTest {

    @Container
    static GenericContainer<?> redis =
            new GenericContainer<>("redis")
                    .withExposedPorts(6379);

    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres")
                    .withDatabaseName("test_db")
                    .withUsername("postgres")
                    .withPassword("postgres");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User user;
    private UserDto dto;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        user = new User();
        user.setName("Roman");
        user.setSurname("Sidorchuk");
        user.setBirthDate(LocalDate.of(2006, 2, 28));
        user.setEmail("romansidorcuk1@gmail.com");
        user.setActive(true);
        user = userRepository.save(user);


        dto = new UserDto();
        dto.setName("Maksim");
        dto.setSurname("Sidorchuk");
        dto.setBirthDate(LocalDate.of(2006, 2, 28));
        dto.setEmail("maksimsidorcuk1@gmail.com");
        dto.setActive(true);
    }

    @Test
    void createUser_ShouldReturnSavedPaymentCard_WhenSuccessful() throws Exception {

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Maksim"))
                .andExpect(jsonPath("$.email").value("maksimsidorcuk1@gmail.com"));

    }

    @Test
    void createUser_ShouldReturn400_WhenInvalidData() throws Exception {

        dto.setName(null);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());

    }

    @Test
    void updateUser_ShouldUpdatePaymentCard_WhenSuccessful() throws Exception {

        mockMvc.perform(put("/users/{id}", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Maksim"))
                .andExpect(jsonPath("$.email").value("maksimsidorcuk1@gmail.com"));

    }

    @Test
    void updateUser_ShouldReturn404_WhenNotFound() throws Exception {

        mockMvc.perform(put("/users/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());

    }

    @Test
    void findById_ShouldReturnPaymentCard_WhenSuccessful() throws Exception {

        mockMvc.perform(get("/users/{id}", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Roman"))
                .andExpect(jsonPath("$.email").value("romansidorcuk1@gmail.com"));

    }

    @Test
    void findById_ShouldReturn404_WhenNotFound() throws Exception {

        mockMvc.perform(get("/users/99"))
                .andExpect(status().isNotFound());

    }

    @Test
    void findSelfById_ShouldReturnCurrentUser() throws Exception {

        MyUserDetails userDetails = MyUserDetails.builder()
                .userId(user.getId())
                .role("ROLE_USER")
                .build();

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

        mockMvc.perform(get("/users/me")
                        .with(authentication(auth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("romansidorcuk1@gmail.com"));
    }

    @Test
    void getUsers_ShouldReturnPaymentCardPage_WhenSuccessful() throws Exception {

        mockMvc.perform(get("/users")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].email").value("romansidorcuk1@gmail.com"));

    }

    @Test
    void activateUser_ShouldChangeActiveStatus_WhenSuccessful() throws Exception {

        user.setActive(false);
        userRepository.save(user);

        mockMvc.perform(patch("/users/{id}/activate", user.getId()))
                .andExpect(status().isNoContent());

        User updated = userRepository.findById(user.getId()).orElseThrow();
        assertEquals(true, updated.isActive());

    }

    @Test
    void activateUser_ShouldReturn404_WhenNotFound() throws Exception {

        mockMvc.perform(patch("/users/99/activate"))
                .andExpect(status().isNotFound());

    }

    @Test
    void deactivateUser_ShouldChangeActiveStatus_WhenSuccessful() throws Exception {

        user.setActive(true);
        userRepository.save(user);

        mockMvc.perform(patch("/users/{id}/deactivate", user.getId()))
                .andExpect(status().isNoContent());

        User updated = userRepository.findById(user.getId()).orElseThrow();
        assertEquals(false, updated.isActive());

    }

    @Test
    void deactivateUser_ShouldReturn404_WhenNotFound() throws Exception {

        mockMvc.perform(patch("/users/99/deactivate"))
                .andExpect(status().isNotFound());

    }

    @Test
    void deleteUser_ShouldRemoveFromDatabase() throws Exception {

        mockMvc.perform(delete("/users/{id}", user.getId()))
                .andExpect(status().isNoContent());

        assertEquals(0, userRepository.count());

    }

}