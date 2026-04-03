package by.innowise.orderservice.controller.controllerImpl;

import by.innowise.orderservice.controller.ItemController;
import by.innowise.orderservice.dto.ItemDto;
import by.innowise.orderservice.dto.create.ItemCreateDto;
import by.innowise.orderservice.service.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemControllerImpl implements ItemController {

    private final ItemService itemService;

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ItemDto> createItem(@RequestBody @Valid ItemCreateDto dto) {
        return new ResponseEntity<>(itemService.createItem(dto), HttpStatus.CREATED);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping
    public ResponseEntity<ItemDto> updateItem(@RequestBody ItemDto itemDto) {
        return new ResponseEntity<>(itemService.updateItem(itemDto), HttpStatus.OK);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping
    public ResponseEntity<Page<ItemDto>> findAll(@RequestParam int page,
                                                 @RequestParam int size) {
        return new ResponseEntity<>(itemService.findAll(page, size), HttpStatus.OK);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<ItemDto> findById(@PathVariable Long id) {
        return new ResponseEntity<>(itemService.findById(id), HttpStatus.OK);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        itemService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
