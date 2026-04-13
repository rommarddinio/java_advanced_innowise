package com.innowise.orderservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a single item within an order.
 * <p>
 * Links an {@link Order} to an {@link Item} and stores the quantity.
 * Extends {@link Auditable} to include audit fields such as createdAt and updatedAt.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "order_items")
public class OrderItem extends Auditable {

    /**
     * Primary key of the order item.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The order to which this item belongs.
     * <p>
     * Many-to-one relationship to {@link Order}.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    /**
     * The item included in the order.
     * <p>
     * Many-to-one relationship to {@link Item}.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    /**
     * Quantity of this item in the order.
     */
    private Integer quantity;

}