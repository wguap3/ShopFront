package ru.yandex.practicum.mymarket.order.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.mymarket.exception.ItemNotFoundException;
import ru.yandex.practicum.mymarket.item.model.Item;
import ru.yandex.practicum.mymarket.item.repository.ItemRepository;
import ru.yandex.practicum.mymarket.order.dto.OrderDto;
import ru.yandex.practicum.mymarket.order.mapper.OrderMapper;
import ru.yandex.practicum.mymarket.order.model.Order;
import ru.yandex.practicum.mymarket.order.repository.OrderRepository;
import ru.yandex.practicum.mymarket.orderItem.model.OrderItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final ItemRepository itemRepository;

    @Override
    public List<OrderDto> getAllOrders() {
        return orderRepository.findAllWithItems().stream()
                .map(orderMapper::toDto)
                .toList();
    }


    @Override
    public OrderDto getOrderById(Long id) {
        Order order = orderRepository.findByIdWithItems(id);
        if (order == null) {
            throw new RuntimeException("Order not found with id " + id);
        }

        return orderMapper.toDto(order);
    }

    @Override
    @Transactional
    public Long createOrderFromCart(Map<Long, Integer> cartItems) {
        if (cartItems.isEmpty()) {
            throw new IllegalStateException("Cart is empty");
        }

        Order order = new Order();
        order.setItems(new ArrayList<>());
        long totalSum = 0;

        for (Map.Entry<Long, Integer> entry : cartItems.entrySet()) {
            Long itemId = entry.getKey();
            Integer count = entry.getValue();

            Item item = itemRepository.findById(itemId)
                    .orElseThrow(() -> new ItemNotFoundException("Item not found with id: " + itemId));

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setItem(item);
            orderItem.setCount(count);
            orderItem.setPrice(item.getPrice());

            totalSum += item.getPrice() * count;
            order.getItems().add(orderItem);
        }

        order.setTotalSum(totalSum);
        Order savedOrder = orderRepository.save(order);
        return savedOrder.getId();
    }

}
