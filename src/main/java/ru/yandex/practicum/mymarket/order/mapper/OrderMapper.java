package ru.yandex.practicum.mymarket.order.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.mymarket.order.dto.OrderDto;
import ru.yandex.practicum.mymarket.order.model.Order;
import ru.yandex.practicum.mymarket.orderItem.mapper.OrderItemMapper;

@Mapper(componentModel = "spring", uses = {OrderItemMapper.class})
public interface OrderMapper {
    @Mapping(target = "items", source = "items")
    OrderDto toDto(Order order);
}

