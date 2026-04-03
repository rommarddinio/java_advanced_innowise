package by.innowise.orderservice.controller;

import by.innowise.orderservice.dto.ItemDto;
import by.innowise.orderservice.dto.create.ItemCreateDto;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;


public interface ItemController {

    ResponseEntity<ItemDto> createItem(ItemCreateDto dto);

    ResponseEntity<ItemDto> updateItem(ItemDto itemDto);

    ResponseEntity<Page<ItemDto>> findAll(int page, int size);

    ResponseEntity<ItemDto> findById(Long id);

    ResponseEntity<Void> deleteById(Long id);

}
