package ru.yandex.practicum.mymarket.cart.service;

import jakarta.servlet.http.HttpSession;

import java.util.Map;

public interface CartService {
    void addToCart(HttpSession session, Long itemId);

    void removeFromCart(HttpSession session, Long itemId);

    void changeCount(HttpSession session, Long itemId, String action);

    Map<Long, Integer> getCartItems(HttpSession session);

    int getCountInCart(HttpSession session, Long itemId);

    long calculateTotalSum(HttpSession session);

    void clearCart(HttpSession session);
}
