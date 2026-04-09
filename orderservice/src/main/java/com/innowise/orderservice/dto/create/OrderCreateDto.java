package com.innowise.orderservice.dto.create;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreateDto {
    @NotBlank(message = "Email should not be empty")
    @Email(message = "Email should be valid")
    private String email;
    private List<OrderItemCreateDto> items;
}
