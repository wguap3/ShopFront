package ru.yandex.practicum.mymarket.order;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.mymarket.item.model.Item;
import ru.yandex.practicum.mymarket.item.repository.ItemRepository;
import ru.yandex.practicum.mymarket.order.dto.OrderDto;
import ru.yandex.practicum.mymarket.order.mapper.OrderMapper;
import ru.yandex.practicum.mymarket.order.model.Order;
import ru.yandex.practicum.mymarket.order.repository.OrderRepository;
import ru.yandex.practicum.mymarket.order.service.OrderServiceImpl;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void getAllOrders_returnsAllOrders() {
        Order order1 = new Order();
        Order order2 = new Order();
        OrderDto dto1 = new OrderDto();
        OrderDto dto2 = new OrderDto();
        when(orderRepository.findAllWithItems()).thenReturn(List.of(order1, order2));
        when(orderMapper.toDto(order1)).thenReturn(dto1);
        when(orderMapper.toDto(order2)).thenReturn(dto2);

        List<OrderDto> result = orderService.getAllOrders();

        assertEquals(2, result.size());
        verify(orderRepository).findAllWithItems();
    }

    @Test
    void getAllOrders_emptyRepository_returnsEmptyList() {
        when(orderRepository.findAllWithItems()).thenReturn(List.of());

        List<OrderDto> result = orderService.getAllOrders();

        assertTrue(result.isEmpty());
    }

    @Test
    void getOrderById_exists_returnsDto() {
        Order order = new Order();
        order.setId(1L);
        OrderDto dto = new OrderDto();
        dto.setId(1L);
        when(orderRepository.findByIdWithItems(1L)).thenReturn(order);
        when(orderMapper.toDto(order)).thenReturn(dto);

        OrderDto result = orderService.getOrderById(1L);

        assertEquals(1L, result.getId());
        verify(orderRepository).findByIdWithItems(1L);
    }

    @Test
    void getOrderById_notFound_throwsException() {
        when(orderRepository.findByIdWithItems(99L)).thenReturn(null);

        assertThrows(RuntimeException.class, () -> orderService.getOrderById(99L));
    }

    @Test
    void createOrderFromCart_emptyCart_throwsException() {
        assertThrows(IllegalStateException.class,
                () -> orderService.createOrderFromCart(Map.of()));
    }

    @Test
    void createOrderFromCart_itemNotFound_throwsException() {
        when(itemRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> orderService.createOrderFromCart(Map.of(99L, 1)));
    }

    @Test
    void createOrderFromCart_validCart_savesOrderAndReturnsId() {
        Item item = new Item();
        item.setId(1L);
        item.setPrice(1500L);

        Order savedOrder = new Order();
        savedOrder.setId(10L);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        Long result = orderService.createOrderFromCart(Map.of(1L, 2));

        assertEquals(10L, result);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void createOrderFromCart_multipleItems_calculatesCorrectTotalSum() {
        Item item1 = new Item();
        item1.setId(1L);
        item1.setPrice(1500L);

        Item item2 = new Item();
        item2.setId(2L);
        item2.setPrice(500L);

        Order savedOrder = new Order();
        savedOrder.setId(1L);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item1));
        when(itemRepository.findById(2L)).thenReturn(Optional.of(item2));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(1L);
            return order;
        });

        orderService.createOrderFromCart(Map.of(1L, 2, 2L, 3));

        verify(orderRepository).save(argThat(order ->
                order.getTotalSum() == (2 * 1500L + 3 * 500L)
        ));
    }

    @Test
    void createOrderFromCart_singleItem_setsCorrectOrderItemFields() {
        Item item = new Item();
        item.setId(1L);
        item.setPrice(2000L);

        Order savedOrder = new Order();
        savedOrder.setId(5L);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        Long orderId = orderService.createOrderFromCart(Map.of(1L, 1));

        assertEquals(5L, orderId);
        verify(orderRepository).save(argThat(order ->
                order.getItems().size() == 1 &&
                        order.getItems().get(0).getCount() == 1 &&
                        order.getItems().get(0).getPrice() == 2000L
        ));
    }
}
