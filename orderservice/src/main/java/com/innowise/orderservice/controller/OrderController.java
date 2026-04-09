package com.innowise.orderservice.controller;

import com.innowise.orderservice.dto.OrderDto;
import com.innowise.orderservice.dto.UpdateStatusDto;
import com.innowise.orderservice.dto.create.OrderCreateDto;
import com.innowise.orderservice.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.List;

/**
 * REST controller for managing orders.
 * <p>
 * Provides endpoints for creating, updating, retrieving, and deleting orders.
 * Supports pagination, filtering by dates and status, and user-specific queries.
 */
public interface OrderController {

    /**
     * Creates a new order.
     *
     * @param dto request body containing order creation data
     * @return {@link ResponseEntity} with created {@link OrderDto}
     */
    ResponseEntity<OrderDto> createOrder(OrderCreateDto dto);

    /**
     * Updates the status of an existing order.
     *
     * @param dto request body containing order ID and new status
     * @return {@link ResponseEntity} with updated {@link OrderDto}
     */
    ResponseEntity<OrderDto> updateById(UpdateStatusDto dto);

    /**
     * Retrieves an order by its ID.
     *
     * @param id unique identifier of the order
     * @return {@link ResponseEntity} with found {@link OrderDto}
     */
    ResponseEntity<OrderDto> findById(Long id);

    /**
     * Retrieves all orders with optional filtering and pagination.
     *
     * @param page      page number (0-based)
     * @param size      number of orders per page
     * @param startDate filter start date (inclusive), may be null
     * @param endDate   filter end date (inclusive), may be null
     * @param status    filter by order status, may be null
     * @return {@link ResponseEntity} containing a {@link Page} of {@link OrderDto}
     */
    ResponseEntity<Page<OrderDto>> findAll(int page, int size, Instant startDate,
                                           Instant endDate, Status status);

    /**
     * Retrieves all orders for a specific user.
     *
     * @param userId unique identifier of the user
     * @return {@link ResponseEntity} containing a list of {@link OrderDto}
     */
    ResponseEntity<List<OrderDto>> findByUserId(Long userId);

    /**
     * Retrieves all orders for the currently authenticated user.
     *
     * @return {@link ResponseEntity} containing a list of {@link OrderDto}
     */
    ResponseEntity<List<OrderDto>> findBySelfId();

    /**
     * Deletes an order by its ID.
     *
     * @param id unique identifier of the order
     * @return {@link ResponseEntity} with no content
     */
    ResponseEntity<Void> deleteById(Long id);

}