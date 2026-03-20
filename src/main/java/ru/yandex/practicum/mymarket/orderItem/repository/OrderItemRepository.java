package ru.yandex.practicum.mymarket.orderItem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.mymarket.orderItem.model.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
