package com.innowise.orderservice.service;

import com.innowise.orderservice.dto.ItemDto;
import com.innowise.orderservice.dto.create.ItemCreateDto;
import org.springframework.data.domain.Page;

/**
 * Service for managing items.
 * <p>
 * Provides methods for creating, updating, retrieving, and deleting items.
 * Also supports pagination for fetching multiple items.
 */
public interface ItemService {

    /**
     * Creates a new item.
     *
     * @param dto data required to create a new item
     * @return created item as {@link ItemDto}
     */
    ItemDto createItem(ItemCreateDto dto);

    /**
     * Updates an existing item.
     *
     * @param itemDto item data to update
     * @return updated item as {@link ItemDto}
     */
    ItemDto updateItem(ItemDto itemDto);

    /**
     * Finds an item by its ID.
     *
     * @param id unique identifier of the item
     * @return found item as {@link ItemDto}
     */
    ItemDto findById(Long id);

    /**
     * Retrieves all items with pagination.
     *
     * @param page page number (0-based)
     * @param size number of items per page
     * @return page of items as {@link Page} of {@link ItemDto}
     */
    Page<ItemDto> findAll(int page, int size);

    /**
     * Deletes an item by its ID.
     *
     * @param id unique identifier of the item
     */
    void deleteById(Long id);

}
