package by.innowise.orderservice.controller;

import by.innowise.orderservice.dto.OrderDto;
import by.innowise.orderservice.dto.UpdateStatusDto;
import by.innowise.orderservice.dto.create.OrderCreateDto;
import by.innowise.orderservice.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.List;

public interface OrderController {

    ResponseEntity<OrderDto> createOrder(OrderCreateDto dto);

    ResponseEntity<OrderDto> updateById(UpdateStatusDto dto);

    ResponseEntity<OrderDto> findById(Long id);

    ResponseEntity<Page<OrderDto>> findAll(int page, int size, Instant startDate,
                                           Instant endDate, Status status);

    ResponseEntity<List<OrderDto>> findByUserId(Long userId);

    ResponseEntity<List<OrderDto>> findBySelfId();

    ResponseEntity<Void> deleteById(Long id);

}
