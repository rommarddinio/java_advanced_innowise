package by.innowise.orderservice.dto.mapping;

import by.innowise.orderservice.dto.OrderDto;
import by.innowise.orderservice.dto.create.OrderCreateDto;
import by.innowise.orderservice.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {OrderItemMapper.class})
public interface OrderMapper {

    Order toEntity(OrderCreateDto dto);

    @Mapping(source = "orderItems", target = "items")
    OrderDto toDto(Order order);

}
