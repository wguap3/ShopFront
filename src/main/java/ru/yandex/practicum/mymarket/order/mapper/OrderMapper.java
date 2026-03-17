package ru.yandex.practicum.mymarket.order.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.mymarket.item.mapper.ItemMapper;
import ru.yandex.practicum.mymarket.order.dto.OrderDto;
import ru.yandex.practicum.mymarket.order.model.Order;

@Mapper(componentModel = "spring", uses = {ItemMapper.class})
public interface OrderMapper {
    OrderDto toDto(Order order);

}
