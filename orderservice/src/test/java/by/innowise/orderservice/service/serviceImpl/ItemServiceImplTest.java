package by.innowise.orderservice.service.serviceImpl;

import by.innowise.orderservice.dto.ItemDto;
import by.innowise.orderservice.dto.create.ItemCreateDto;
import by.innowise.orderservice.dto.mapping.ItemMapper;
import by.innowise.orderservice.entity.Item;
import by.innowise.orderservice.exception.ItemNotFoundException;
import by.innowise.orderservice.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ItemMapper itemMapper;

    @InjectMocks
    private ItemServiceImpl itemService;

    private Item item;
    private Item updatedItem;
    private ItemCreateDto createDto;
    private ItemDto itemDto;
    private ItemDto updateItemDto;

    @BeforeEach
    void setUp() {
        item = new Item();
        item.setId(1L);
        item.setName("Book");
        item.setPrice(BigDecimal.valueOf(10));

        createDto = new ItemCreateDto();
        createDto.setName("Book");
        createDto.setPrice(BigDecimal.valueOf(10));

        itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setPrice(item.getPrice());

        updateItemDto = new ItemDto();
        updateItemDto.setId(item.getId());
        updateItemDto.setName("Book1");
        updateItemDto.setPrice(BigDecimal.valueOf(9.99));

        updatedItem = new Item();
        updatedItem.setId(item.getId());
        updatedItem.setName(updateItemDto.getName());
        updatedItem.setPrice(updateItemDto.getPrice());
    }

    @Test
    void createItem_ShouldReturnItemDto_WhenSuccessful() {
        when(itemMapper.toEntity(createDto)).thenReturn(item);
        when(itemRepository.save(item)).thenReturn(item);
        when(itemMapper.toDto(item)).thenReturn(itemDto);

        ItemDto result = itemService.createItem(createDto);

        assertNotNull(result);
        assertEquals(item.getName(), result.getName());

        verify(itemMapper).toEntity(createDto);
        verify(itemRepository).save(item);
        verify(itemMapper).toDto(item);
    }

    @Test
    void updateItem_ShouldReturnItemDto_WhenSuccessful() {
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(itemRepository.save(item)).thenReturn(updatedItem);
        when(itemMapper.toDto(updatedItem)).thenReturn(updateItemDto);

        ItemDto result = itemService.updateItem(updateItemDto);

        assertNotNull(result);
        assertEquals(updatedItem.getName(), result.getName());

        verify(itemRepository).findById(item.getId());
        verify(itemRepository).save(item);
        verify(itemMapper).toDto(updatedItem);
    }

    @Test
    void updateItem_ShouldThrowException_WhenItemNotFound() {
        updateItemDto.setId(99L);
        when(itemRepository.findById(updateItemDto.getId())).thenThrow(ItemNotFoundException.class);

        assertThrows(ItemNotFoundException.class,() -> itemService.updateItem(updateItemDto));

        verify(itemRepository).findById(updateItemDto.getId());
    }

    @Test
    void findById_ShouldReturnItemDto_WhenSuccessful() {
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(itemMapper.toDto(item)).thenReturn(itemDto);

        ItemDto result = itemService.findById(item.getId());

        assertNotNull(result);
        assertEquals(item.getName(), result.getName());

        verify(itemRepository).findById(item.getId());
        verify(itemMapper).toDto(item);
    }

    @Test
    void findById_ShouldThrowException_WhenNotFound() {
        item.setId(99L);
        when(itemRepository.findById(item.getId())).thenThrow(ItemNotFoundException.class);

        assertThrows(ItemNotFoundException.class, () -> itemService.findById(item.getId()));

        verify(itemRepository).findById(item.getId());
    }

    @Test
    void findAll_ShouldReturnPageOfItemDto() {
        int page = 0;
        int size = 10;


        Page<Item> itemPage = new PageImpl<>(List.of(item),
                PageRequest.of(page, size), 1);

        when(itemRepository.findAll(any(PageRequest.class))).thenReturn(itemPage);
        when(itemMapper.toDto(item)).thenReturn(itemDto);

        Page<ItemDto> result = itemService.findAll(page, size);

        assertNotNull(result);
        assertEquals(item.getName(), result.getContent().getFirst().getName());
        assertEquals(1, result.getTotalElements());

        verify(itemRepository).findAll(any(PageRequest.class));
        verify(itemMapper).toDto(item);
    }

    @Test
    void deleteById_ShouldReturnNothing_WhenSuccessful() {
        when(itemRepository.softDeleteById(item.getId())).thenReturn(1);

        itemService.deleteById(item.getId());

        verify(itemRepository).softDeleteById(item.getId());
    }

    @Test
    void deleteById_ShouldThrowException_WhenNotFound() {
        item.setId(99L);
        when(itemRepository.softDeleteById(item.getId())).thenReturn(0);

        assertThrows(ItemNotFoundException.class, () -> itemService.deleteById(item.getId()));

        verify(itemRepository).softDeleteById(item.getId());
    }
}
