package com.innowise.orderservice.entity;

import com.innowise.orderservice.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.math.BigDecimal;
import java.util.List;

/**
 * Represents a customer order in the system.
 * <p>
 * Each order belongs to a user and contains multiple {@link OrderItem} entities.
 * Tracks total price, status, and soft delete flag.
 * Extends {@link Auditable} to include audit fields such as createdAt and updatedAt.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orders")
@SQLDelete(sql = "UPDATE orders SET deleted = true WHERE id = ?")
@Where(clause = "deleted = false")
public class Order extends Auditable {

    /**
     * Primary key of the order.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Identifier of the user who placed the order.
     */
    @Column(name = "user_id")
    private Long userId;

    /**
     * Status of the order.
     * <p>
     * Stored as string in the database.
     */
    @Enumerated(EnumType.STRING)
    private Status status;

    /**
     * Total price of the order.
     * <p>
     * Precision is 10 with 2 decimal places.
     */
    @Column(name = "total_price", precision = 10, scale = 2)
    private BigDecimal totalPrice;

    /**
     * Soft delete flag.
     * <p>
     * True if the order is deleted, false otherwise.
     */
    private Boolean deleted;

    /**
     * List of order items included in this order.
     * <p>
     * Bidirectional OneToMany relationship with {@link OrderItem}.
     */
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems;

}