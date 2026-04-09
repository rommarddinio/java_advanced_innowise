package com.innowise.orderservice.service;

import com.innowise.orderservice.dto.OrderDto;
import com.innowise.orderservice.dto.create.OrderCreateDto;
import com.innowise.orderservice.enums.Status;
import org.springframework.data.domain.Page;

import java.time.Instant;
import java.util.List;

/**
 * Service for managing orders.
 * <p>
 * Provides business logic for creating, updating, retrieving, and deleting orders.
 * Supports filtering, pagination, and user-related queries.
 */
public interface OrderService {

    /**
     * Creates a new order.
     * <p>
     * The order is created based on provided data, items are validated,
     * and total price is calculated.
     *
     * @param orderDto data required to create an order
     * @return created order as {@link OrderDto}
     */
    OrderDto createOrder(OrderCreateDto orderDto);

    /**
     * Retrieves an order by its ID.
     *
     * @param id unique identifier of the order
     * @return found order as {@link OrderDto}
     */
    OrderDto findById(Long id);

    /**
     * Retrieves all orders for a specific user.
     *
     * @param id user identifier
     * @return list of orders belonging to the user
     */
    List<OrderDto> findByUserId(Long id);

    /**
     * Retrieves all orders of the currently authenticated user.
     *
     * @return list of orders for the current user
     */
    List<OrderDto> findBySelfId();

    /**
     * Retrieves all orders with optional filtering and pagination.
     *
     * @param page      page number (0-based)
     * @param size      number of records per page
     * @param startDate start date filter (inclusive), may be null
     * @param endDate   end date filter (inclusive), may be null
     * @param status    order status filter, may be null
     * @return paginated list of orders
     */
    Page<OrderDto> findAll(int page, int size, Instant startDate, Instant endDate, Status status);

    /**
     * Deletes an order by its ID.
     * <p>
     * May perform a soft delete depending on implementation.
     *
     * @param id unique identifier of the order
     */
    void deleteById(Long id);

    /**
     * Updates the status of an existing order.
     *
     * @param id     unique identifier of the order
     * @param status new status to set
     * @return updated order as {@link OrderDto}
     */
    OrderDto updateById(Long id, Status status);
}
