package by.innowise.orderservice.service;

import by.innowise.orderservice.dto.ItemDto;
import by.innowise.orderservice.dto.create.ItemCreateDto;
import org.springframework.data.domain.Page;


public interface ItemService {

    ItemDto createItem(ItemCreateDto dto);

    ItemDto updateItem(ItemDto itemDto);

    ItemDto findById(Long id);

    Page<ItemDto> findAll(int page, int size);

    void deleteById(Long id);

}
