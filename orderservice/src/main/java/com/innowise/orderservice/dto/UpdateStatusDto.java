package com.innowise.orderservice.dto;

import com.innowise.orderservice.enums.Status;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStatusDto {
    @NotNull(message = "ID should not be null")
    @Min(value = 1, message = "ID must be greater than 0")
    private Long id;

    @NotNull(message = "Status should not be null")
    private Status status;
}
