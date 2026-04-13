package com.innowise.orderservice.dto.mapping;

import com.innowise.orderservice.dto.OrderItemDto;
import com.innowise.orderservice.dto.create.OrderItemCreateDto;
import com.innowise.orderservice.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    @Mapping(source = "item", target = "itemDto")
    OrderItemDto toDto(OrderItem orderItem);

    OrderItem toEntity(OrderItemCreateDto dto);

}
