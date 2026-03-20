package ru.yandex.practicum.mymarket.order.service;

import ru.yandex.practicum.mymarket.order.dto.OrderDto;

import java.util.List;
import java.util.Map;

public interface OrderService {
    List<OrderDto> getAllOrders();

    OrderDto getOrderById(Long id);

    Long createOrderFromCart(Map<Long, Integer> cartItems);
}
