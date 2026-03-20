package ru.yandex.practicum.mymarket.order.dto;

import lombok.Getter;
import lombok.Setter;
import ru.yandex.practicum.mymarket.orderItem.dto.OrderItemDto;

import java.util.List;

@Getter
@Setter
public class OrderDto {
    private Long id;
    private List<OrderItemDto> items;
    private long totalSum;
}
