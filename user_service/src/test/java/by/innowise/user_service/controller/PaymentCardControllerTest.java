package by.innowise.user_service.controller;

import by.innowise.user_service.dto.PaymentCardDto;
import by.innowise.user_service.entity.PaymentCard;
import by.innowise.user_service.entity.User;
import by.innowise.user_service.repository.PaymentCardRepository;
import by.innowise.user_service.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
class PaymentCardControllerTest{

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
    private PaymentCardRepository paymentCardRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User user;
    private PaymentCard paymentCard;
    private PaymentCardDto dto;

    @BeforeEach
    void setUp() {
        paymentCardRepository.deleteAll();
        userRepository.deleteAll();

        user = new User();
        user.setName("Roman");
        user.setSurname("Sidorchuk");
        user.setBirthDate(LocalDate.of(2006, 2, 28));
        user.setEmail("romansidorcuk1@gmail.com");
        user.setActive(true);
        user = userRepository.save(user);

        paymentCard = new PaymentCard();
        paymentCard.setNumber("1111 1111 1111 1111");
        paymentCard.setHolder("RAMAN SIDARCHUK");
        paymentCard.setExpirationDate(LocalDate.of(2030, 12, 31));
        paymentCard.setActive(true);
        paymentCard.setUser(user);
        paymentCard = paymentCardRepository.save(paymentCard);

        dto = new PaymentCardDto();
        dto.setNumber("2222 2222 2222 2222");
        dto.setExpirationDate(LocalDate.of(2030, 12, 31));
        dto.setHolder("RAMAN BEBRA");
        dto.setUserId(user.getId());
        dto.setActive(true);
    }


    @Test
    void createPaymentCard_ShouldReturnSavedPaymentCard_WhenSuccessful() throws Exception {

        mockMvc.perform(post("/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.number").value("2222 2222 2222 2222"))
                .andExpect(jsonPath("$.holder").value("RAMAN BEBRA"));

    }

    @Test
    void updatePaymentCard_ShouldUpdatePaymentCard_WhenSuccessful() throws Exception {

        mockMvc.perform(put("/cards/{id}", paymentCard.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value("2222 2222 2222 2222"))
                .andExpect(jsonPath("$.holder").value("RAMAN BEBRA"));

    }

    @Test
    void findById_ShouldReturnPaymentCard_WhenSuccessful() throws Exception {

        Long id = paymentCard.getId();

        mockMvc.perform(get("/cards/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value(paymentCard.getNumber()))
                .andExpect(jsonPath("$.holder").value(paymentCard.getHolder()));

    }

    @Test
    void findByUserId_ShouldReturnListOfPaymentCards() throws Exception {

        mockMvc.perform(get("/cards/byUser/{id}", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

    }

    @Test
    void findByUserId_ShouldReturn404_WhenUserNotFound() throws Exception{

        mockMvc.perform(get("/cards/byUser/99"))
                .andExpect(status().isNotFound());

    }

    @Test
    void findById_ShouldReturn404_WhenNotFound() throws Exception {

        mockMvc.perform(get("/cards/99"))
                .andExpect(status().isNotFound());

    }

    @Test
    void getPaymentCards_ShouldReturnPaymentCardPage_WhenSuccessful() throws Exception {

        mockMvc.perform(get("/cards")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].number").value("1111 1111 1111 1111"));

    }

    @Test
    void activatePaymentCard_ShouldChangeActiveStatus_WhenSuccessful() throws Exception {

        mockMvc.perform(patch("/cards/{id}/activate", paymentCard.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void activatePaymentCard_ShouldReturn404_WhenNotFound() throws Exception {

        mockMvc.perform(patch("/cards/99/activate"))
                .andExpect(status().isNotFound());

    }

    @Test
    void deactivatePaymentCard_ShouldChangeActiveStatus_WhenSuccessful() throws Exception {

        mockMvc.perform(patch("/cards/{id}/deactivate", paymentCard.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void deactivatePaymentCard_ShouldReturn404_WhenNotFound() throws Exception {

        mockMvc.perform(patch("/cards/99/deactivate"))
                .andExpect(status().isNotFound());

    }

    @Test
    void deletePaymentCard_ShouldRemoveFromDatabase() throws Exception {

        mockMvc.perform(delete("/cards/{id}", paymentCard.getId()))
                .andExpect(status().isNoContent());

        assertEquals(0, paymentCardRepository.count());

    }
}