package com.innowise.orderservice.controller;

import com.innowise.orderservice.dto.ItemDto;
import com.innowise.orderservice.dto.create.ItemCreateDto;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

/**
 * REST controller for managing items.
 * <p>
 * Provides endpoints for creating, updating, retrieving, and deleting items.
 */
public interface ItemController {

    /**
     * Creates a new item.
     *
     * @param dto request body containing item creation data
     * @return {@link ResponseEntity} with created {@link ItemDto}
     */
    ResponseEntity<ItemDto> createItem(ItemCreateDto dto);

    /**
     * Updates an existing item.
     *
     * @param itemDto request body containing updated item data
     * @return {@link ResponseEntity} with updated {@link ItemDto}
     */
    ResponseEntity<ItemDto> updateItem(ItemDto itemDto);

    /**
     * Retrieves all items with pagination.
     *
     * @param page page number (0-based)
     * @param size number of items per page
     * @return {@link ResponseEntity} containing a page of {@link ItemDto}
     */
    ResponseEntity<Page<ItemDto>> findAll(int page, int size);

    /**
     * Retrieves an item by its ID.
     *
     * @param id unique identifier of the item
     * @return {@link ResponseEntity} with found {@link ItemDto}
     */
    ResponseEntity<ItemDto> findById(Long id);

    /**
     * Deletes an item by its ID.
     *
     * @param id unique identifier of the item
     * @return {@link ResponseEntity} with no content
     */
    ResponseEntity<Void> deleteById(Long id);

}