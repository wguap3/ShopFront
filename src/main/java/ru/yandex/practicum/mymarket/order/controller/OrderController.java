package ru.yandex.practicum.mymarket.order.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.mymarket.cart.service.CartService;
import ru.yandex.practicum.mymarket.order.dto.OrderDto;
import ru.yandex.practicum.mymarket.order.service.OrderService;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final CartService cartService;

    @GetMapping
    public String getOrdersPage(Model model) {
        List<OrderDto> orders = orderService.getAllOrders();
        model.addAttribute("orders", orders);
        return "orders";
    }

    @GetMapping("/{id}")
    public String getOrderPage(
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "false") boolean newOrder,
            Model model) {

        OrderDto order = orderService.getOrderById(id);
        model.addAttribute("order", order);
        model.addAttribute("newOrder", newOrder);
        return "order";
    }

    @PostMapping("/buy")
    public String placeOrder(HttpSession session) {
        Map<Long, Integer> cartItems = cartService.getCartItems(session);
        Long orderId = orderService.createOrderFromCart(cartItems);
        cartService.clearCart(session);
        return String.format("redirect:/orders/%d?newOrder=true", orderId);
    }
}
