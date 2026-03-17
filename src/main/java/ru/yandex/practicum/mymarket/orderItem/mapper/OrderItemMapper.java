package ru.yandex.practicum.mymarket.orderItem.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.mymarket.item.mapper.ItemMapper;
import ru.yandex.practicum.mymarket.orderItem.dto.OrderItemDto;
import ru.yandex.practicum.mymarket.orderItem.model.OrderItem;

@Mapper(componentModel = "spring", uses = {ItemMapper.class})
public interface OrderItemMapper {
    @Mapping(source = "item.id", target = "itemId")
    OrderItemDto toOrderItemDto(OrderItem orderItem);
}
