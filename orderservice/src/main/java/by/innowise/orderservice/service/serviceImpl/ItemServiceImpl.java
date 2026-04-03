package by.innowise.orderservice.service.serviceImpl;

import by.innowise.orderservice.dto.ItemDto;
import by.innowise.orderservice.dto.create.ItemCreateDto;
import by.innowise.orderservice.dto.mapping.ItemMapper;
import by.innowise.orderservice.entity.Item;
import by.innowise.orderservice.exception.ItemNotFoundException;
import by.innowise.orderservice.repository.ItemRepository;
import by.innowise.orderservice.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;

    @Override
    public ItemDto createItem(ItemCreateDto dto) {
        Item item = itemMapper.toEntity(dto);

        item.setDeleted(false);

        return itemMapper.toDto(itemRepository.save(item));
    }

    @Transactional
    @Override
    public ItemDto updateItem(ItemDto itemDto) {
        Item item = itemRepository.findById(itemDto.getId()).orElseThrow(ItemNotFoundException::new);

        item.setName(itemDto.getName());
        item.setPrice(itemDto.getPrice());

        return itemMapper.toDto(itemRepository.save(item));
    }

    @Override
    public ItemDto findById(Long id) {
        return itemMapper.toDto(itemRepository.findById(id)
                .orElseThrow(ItemNotFoundException::new));
    }

    @Override
    public Page<ItemDto> findAll(int page, int size) {
        return itemRepository.findAll(PageRequest.of(page, size)).map(itemMapper::toDto);
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        int rows = itemRepository.softDeleteById(id);
        if(rows == 0) throw new ItemNotFoundException();
    }

}
