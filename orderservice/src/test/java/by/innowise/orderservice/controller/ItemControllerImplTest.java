package by.innowise.orderservice.controller;

import by.innowise.orderservice.dto.ItemDto;
import by.innowise.orderservice.dto.create.ItemCreateDto;
import by.innowise.orderservice.dto.user.MyUserDetails;
import by.innowise.orderservice.entity.Item;
import by.innowise.orderservice.repository.ItemRepository;
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
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = "JWT_SECRET=testsecret")
class ItemControllerImplTest {

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
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Item testItem;
    private ItemCreateDto createDto;
    private MyUserDetails admin;

    @BeforeEach
    void setUp() {
        admin = new MyUserDetails(2L, "ROLE_ADMIN");
        itemRepository.deleteAll();

        testItem = new Item();
        testItem.setName("Test Item");
        testItem.setPrice(BigDecimal.valueOf(100));
        testItem.setDeleted(false);
        testItem = itemRepository.save(testItem);

        createDto = new ItemCreateDto("New Item", BigDecimal.valueOf(200));
    }

    @Test
    void createItem_ShouldReturnCreatedItem_WhenSuccessful() throws Exception {
        mockMvc.perform(post("/items")
                        .with(user(admin))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("New Item"))
                .andExpect(jsonPath("$.price").value(200));
    }

    @Test
    void updateItem_ShouldReturnUpdatedItem_WhenSuccessful() throws Exception {
        ItemDto dto = new ItemDto();
        dto.setId(testItem.getId());
        dto.setName("Updated Item");
        dto.setPrice(BigDecimal.valueOf(150));

        mockMvc.perform(put("/items")
                        .with(user(admin))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Item"))
                .andExpect(jsonPath("$.price").value(150));
    }

    @Test
    void findById_ShouldReturnItem_WhenSuccessful() throws Exception {
        mockMvc.perform(get("/items/{id}", testItem.getId())
                        .with(user(admin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Item"))
                .andExpect(jsonPath("$.price").value(100));
    }

    @Test
    void findAll_ShouldReturnItemPage_WhenSuccessful() throws Exception {
        mockMvc.perform(get("/items")
                        .param("page", "0")
                        .param("size", "10")
                        .with(user(admin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Test Item"));
    }

    @Test
    void deleteItem_ShouldRemoveItem_WhenSuccessful() throws Exception {
        mockMvc.perform(patch("/items/{id}", testItem.getId())
                        .with(user(admin)))
                .andExpect(status().isOk());

        Item deleted = itemRepository.findById(testItem.getId()).orElseThrow();
        assertTrue(deleted.getDeleted());
    }

    @Test
    void updateItem_ShouldReturn404_WhenItemNotFound() throws Exception {
        ItemDto dto = new ItemDto();
        dto.setId(999L);
        dto.setName("Item");
        dto.setPrice(BigDecimal.valueOf(123));

        mockMvc.perform(put("/items")
                        .with(user(admin))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void findById_ShouldReturn404_WhenItemNotFound() throws Exception {
        mockMvc.perform(get("/items/{id}", 999L)
                        .with(user(admin)))
                .andExpect(status().isNotFound());
    }
}