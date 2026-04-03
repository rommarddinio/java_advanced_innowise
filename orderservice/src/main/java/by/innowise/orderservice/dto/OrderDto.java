package by.innowise.orderservice.dto;

import by.innowise.orderservice.dto.user.UserInfo;
import by.innowise.orderservice.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
    private Long id;
    private Status status;
    private BigDecimal totalPrice;
    private UserInfo userInfo;
    private Instant createdAt;
    private List<OrderItemDto> items;
}
