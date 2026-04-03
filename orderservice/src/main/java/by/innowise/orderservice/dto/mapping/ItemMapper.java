package by.innowise.orderservice.dto.mapping;

import by.innowise.orderservice.dto.ItemDto;
import by.innowise.orderservice.dto.create.ItemCreateDto;
import by.innowise.orderservice.entity.Item;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "orders", ignore = true)
    Item toEntity(ItemCreateDto itemDto);

    ItemDto toDto(Item item);

}
