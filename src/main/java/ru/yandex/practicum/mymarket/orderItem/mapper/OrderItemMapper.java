package ru.yandex.practicum.mymarket.orderItem.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.mymarket.orderItem.dto.OrderItemDto;
import ru.yandex.practicum.mymarket.orderItem.model.OrderItem;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {
    @Mapping(source = "item.id", target = "itemId")
    @Mapping(source = "item.title", target = "title")
    @Mapping(source = "item.price", target = "price")
    @Mapping(source = "item.imgPath", target = "imgPath")
    OrderItemDto toOrderItemDto(OrderItem orderItem);
}