package ru.yandex.practicum.mymarket.order.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.yandex.practicum.mymarket.item.model.Item;
import ru.yandex.practicum.mymarket.item.repository.ItemRepository;
import ru.yandex.practicum.mymarket.order.model.Order;

import java.util.Map;

public interface OrderService {
    Long createOrderFromCart(Map<Long, Integer> cartItems);
    Page<Order> findAllOrders(Pageable pageable);
    Order findOrderWithItems(Long id);
}
