package by.innowise.user_service.controller;

import by.innowise.user_service.dto.MyUserDetails;
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
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = "JWT_SECRET=testsecret")
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

    private User testUser;
    private PaymentCard paymentCard;
    private PaymentCardDto dto;
    private MyUserDetails user;
    private MyUserDetails admin;

    @BeforeEach
    void setUp() {
        paymentCardRepository.deleteAll();
        userRepository.deleteAll();

        testUser = new User();
        testUser.setName("Roman");
        testUser.setSurname("Sidorchuk");
        testUser.setBirthDate(LocalDate.of(2006, 2, 28));
        testUser.setEmail("romansidorcuk1@gmail.com");
        testUser.setActive(true);
        testUser = userRepository.save(testUser);

        user = new MyUserDetails(testUser.getId(), "ROLE_USER");
        admin = new MyUserDetails(testUser.getId(), "ROLE_ADMIN");

        paymentCard = new PaymentCard();
        paymentCard.setNumber("1111 1111 1111 1111");
        paymentCard.setHolder("RAMAN SIDARCHUK");
        paymentCard.setExpirationDate(LocalDate.of(2030, 12, 31));
        paymentCard.setActive(true);
        paymentCard.setUser(testUser);
        paymentCard = paymentCardRepository.save(paymentCard);

        dto = new PaymentCardDto();
        dto.setNumber("2222 2222 2222 2222");
        dto.setExpirationDate(LocalDate.of(2030, 12, 31));
        dto.setHolder("RAMAN BEBRA");
        dto.setUserId(testUser.getId());
        dto.setActive(true);
    }


    @Test
    void createPaymentCard_ShouldReturnSavedPaymentCard_WhenSuccessful() throws Exception {

        mockMvc.perform(post("/cards")
                        .with(user(admin))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.number").value("2222 2222 2222 2222"))
                .andExpect(jsonPath("$.holder").value("RAMAN BEBRA"));

    }

    @Test
    void updatePaymentCard_ShouldUpdatePaymentCard_WhenSuccessful() throws Exception {

        mockMvc.perform(put("/cards/{id}", paymentCard.getId())
                        .with(user(admin))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value("2222 2222 2222 2222"))
                .andExpect(jsonPath("$.holder").value("RAMAN BEBRA"));

    }

    @Test
    void createPaymentCard_ShouldReturn409_WhenCardLimitReached() throws Exception {

        PaymentCard paymentCard2 = new PaymentCard();
        paymentCard2.setNumber("1222 2222 2222 2222");
        paymentCard2.setHolder("RAMAN SIDARCHUK");
        paymentCard2.setExpirationDate(LocalDate.of(2030, 12, 31));
        paymentCard2.setActive(true);
        paymentCard2.setUser(testUser);
        paymentCardRepository.save(paymentCard2);

        PaymentCard paymentCard3 = new PaymentCard();
        paymentCard3.setNumber("3333 3333 3333 3333");
        paymentCard3.setHolder("RAMAN SIDARCHUK");
        paymentCard3.setExpirationDate(LocalDate.of(2030, 12, 31));
        paymentCard3.setActive(true);
        paymentCard3.setUser(testUser);
        paymentCardRepository.save(paymentCard3);

        PaymentCard paymentCard4 = new PaymentCard();
        paymentCard4.setNumber("4444 4444 4444 4444");
        paymentCard4.setHolder("RAMAN SIDARCHUK");
        paymentCard4.setExpirationDate(LocalDate.of(2030, 12, 31));
        paymentCard4.setActive(true);
        paymentCard4.setUser(testUser);
        paymentCardRepository.save(paymentCard4);

        PaymentCard paymentCard5 = new PaymentCard();
        paymentCard5.setNumber("5555 5555 5555 5555");
        paymentCard5.setHolder("RAMAN SIDARCHUK");
        paymentCard5.setExpirationDate(LocalDate.of(2030, 12, 31));
        paymentCard5.setActive(true);
        paymentCard5.setUser(testUser);
        paymentCardRepository.save(paymentCard5);

        mockMvc.perform(post("/cards")
                        .with(user(admin))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict());

    }

    @Test
    void updatePaymentCard_ShouldReturn404_WhenNotFound() throws Exception {

        mockMvc.perform(put("/cards/999")
                        .with(user(admin))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void createPaymentCard_ShouldReturn404_WhenUserNotFound() throws Exception {

        dto.setUserId(999L);

        mockMvc.perform(post("/cards")
                        .with(user(admin))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void findById_ShouldReturnPaymentCard_WhenSuccessful() throws Exception {

        Long id = paymentCard.getId();

        mockMvc.perform(get("/cards/{id}", id)
                        .with(user(admin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value(paymentCard.getNumber()))
                .andExpect(jsonPath("$.holder").value(paymentCard.getHolder()));

    }

    @Test
    void findByUserId_ShouldReturnListOfPaymentCards() throws Exception {

        mockMvc.perform(get("/cards/byUser/{id}", testUser.getId())
                        .with(user(admin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

    }

    @Test
    void findByUserId_ShouldReturn404_WhenUserNotFound() throws Exception{

        mockMvc.perform(get("/cards/byUser/99")
                        .with(user(admin)))
                .andExpect(status().isNotFound());

    }

    @Test
    void findById_ShouldReturn404_WhenNotFound() throws Exception {

        mockMvc.perform(get("/cards/99")
                        .with(user(admin)))
                .andExpect(status().isNotFound());

    }

    @Test
    void getPaymentCards_ShouldReturnPaymentCardPage_WhenSuccessful() throws Exception {

        mockMvc.perform(get("/cards")
                        .param("page", "0")
                        .param("size", "10")
                        .with(user(admin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].number").value("1111 1111 1111 1111"));

    }

    @Test
    void activatePaymentCard_ShouldChangeActiveStatus_WhenSuccessful() throws Exception {

        mockMvc.perform(patch("/cards/{id}/activate", paymentCard.getId())
                        .with(user(admin)))
                .andExpect(status().isNoContent());
    }

    @Test
    void activatePaymentCard_ShouldReturn404_WhenNotFound() throws Exception {

        mockMvc.perform(patch("/cards/99/activate")
                        .with(user(admin)))
                .andExpect(status().isNotFound());

    }

    @Test
    void deactivatePaymentCard_ShouldChangeActiveStatus_WhenSuccessful() throws Exception {

        mockMvc.perform(patch("/cards/{id}/deactivate", paymentCard.getId())
                        .with(user(admin)))
                .andExpect(status().isNoContent());
    }

    @Test
    void deactivatePaymentCard_ShouldReturn404_WhenNotFound() throws Exception {

        mockMvc.perform(patch("/cards/99/deactivate")
                        .with(user(admin)))
                .andExpect(status().isNotFound());

    }

    @Test
    void deletePaymentCard_ShouldRemoveFromDatabase() throws Exception {

        mockMvc.perform(delete("/cards/{id}", paymentCard.getId())
                        .with(user(admin)))
                .andExpect(status().isNoContent());

        assertEquals(0, paymentCardRepository.count());

    }

    @Test
    void deactivatePaymentCard_ShouldTReturn401_WhenNoAccess() throws Exception {

        mockMvc.perform(patch("/cards/1/deactivate")
                        .with(user(user)))
                .andExpect(status().isForbidden());

    }

    @Test
    void activatePaymentCard_ShouldTReturn401_WhenNoAccess() throws Exception {

        mockMvc.perform(patch("/cards/1/activate")
                        .with(user(user)))
                .andExpect(status().isForbidden());

    }

    @Test
    void deletePaymentCard_ShouldTReturn401_WhenNoAccess() throws Exception {

        mockMvc.perform(delete("/cards/{id}", paymentCard.getId())
                        .with(user(user)))
                .andExpect(status().isForbidden());

    }
}