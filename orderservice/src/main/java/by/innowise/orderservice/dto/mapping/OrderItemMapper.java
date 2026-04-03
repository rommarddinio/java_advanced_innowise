package by.innowise.orderservice.dto.mapping;

import by.innowise.orderservice.dto.OrderItemDto;
import by.innowise.orderservice.dto.create.OrderItemCreateDto;
import by.innowise.orderservice.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    @Mapping(source = "item", target = "itemDto")
    OrderItemDto toDto(OrderItem orderItem);

    OrderItem toEntity(OrderItemCreateDto dto);

}
