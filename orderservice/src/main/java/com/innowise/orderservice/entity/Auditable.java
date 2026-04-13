package com.innowise.orderservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

/**
 * Base class for auditable entities.
 * <p>
 * Provides automatic tracking of entity creation and last modification timestamps.
 * Entities extending this class will automatically have {@code createdAt} and
 * {@code updatedAt} fields populated by Spring Data JPA auditing.
 */
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class Auditable {

    /**
     * Timestamp when the entity was created.
     * <p>
     * Automatically set by JPA auditing, cannot be updated.
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    /**
     * Timestamp when the entity was last modified.
     * <p>
     * Automatically updated by JPA auditing whenever the entity is updated.
     */
    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;

}