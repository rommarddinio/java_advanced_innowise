package com.innowise.orderservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.math.BigDecimal;
import java.util.List;

/**
 * Represents an item that can be included in orders.
 * <p>
 * Each item has a name, price, and a soft delete flag.
 * It maintains a bidirectional relationship with {@link OrderItem}.
 * Extends {@link Auditable} to include audit fields such as createdAt and updatedAt.
 */
@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "items")
public class Item extends Auditable {

    /**
     * Primary key of the item.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Name of the item.
     */
    private String name;

    /**
     * Price of the item.
     */
    private BigDecimal price;

    /**
     * Soft delete flag.
     * <p>
     * True if the item is deleted, false otherwise.
     */
    private Boolean deleted;

    /**
     * List of order items that include this item.
     * <p>
     * Bidirectional OneToMany relationship with {@link OrderItem}.
     * Uses batch fetching for performance optimization.
     */
    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
    @BatchSize(size = 10)
    private List<OrderItem> orders;

}