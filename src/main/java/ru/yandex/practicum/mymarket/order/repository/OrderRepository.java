package ru.yandex.practicum.mymarket.order.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.yandex.practicum.mymarket.order.model.Order;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Page<Order> findAll(Pageable pageable);

    @Query("SELECT o FROM Order o " +
            "LEFT JOIN FETCH o.items oi " +
            "LEFT JOIN FETCH oi.item " +
            "WHERE o.id = :id")
    Order findByIdWithItems(@Param("id") Long id);

    @Query("SELECT o FROM Order o " +
            "LEFT JOIN FETCH o.items oi " +
            "LEFT JOIN FETCH oi.item")
    List<Order> findAllWithItems();
}
