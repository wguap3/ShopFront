package ru.yandex.practicum.mymarket.orderItem.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.yandex.practicum.mymarket.item.model.Item;
import ru.yandex.practicum.mymarket.order.model.Order;

@Entity
@Table(name = "order_items")
@Getter
@Setter
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Column(nullable = false)
    private Integer count;

    @Column(nullable = false)
    private Long price;

    public Long getTotalPrice() {
        return this.price * this.count;
    }
}
