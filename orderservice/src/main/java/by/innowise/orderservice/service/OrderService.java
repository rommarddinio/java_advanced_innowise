package by.innowise.orderservice.service;

import by.innowise.orderservice.dto.OrderDto;
import by.innowise.orderservice.dto.create.OrderCreateDto;
import by.innowise.orderservice.enums.Status;
import org.springframework.data.domain.Page;

import java.time.Instant;
import java.util.List;

public interface OrderService {

    OrderDto createOrder(OrderCreateDto orderDto);

    OrderDto findById(Long id);

    List<OrderDto> findByUserId(Long id);

    List<OrderDto> findBySelfId();

    Page<OrderDto> findAll(int page, int size, Instant startDate, Instant endDate, Status status);

    void deleteById(Long id);

    OrderDto updateById(Long id, Status status);
}
