package ru.yandex.practicum.mymarket.order.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.mymarket.order.model.Order;

public interface OrderRepository extends JpaRepository<Order,Long> {
    Page<Order> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"items"})
    Order findByIdWithItems(Long id);
}
