package ru.yandex.practicum.mymarket.cart.service;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.mymarket.item.model.Item;
import ru.yandex.practicum.mymarket.item.repository.ItemRepository;

import java.util.HashMap;
import java.util.Map;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private static final String CART_KEY = "cart";
    private final ItemRepository itemRepository;

    private Map<Long, Integer> getCart(HttpSession session) {
        Map<Long, Integer> cart = (Map<Long, Integer>) session.getAttribute(CART_KEY);
        if (cart == null) {
            cart = new HashMap<>();
            session.setAttribute(CART_KEY, cart);
        }
        return cart;
    }

    @Override
    @Transactional
    public void addToCart(HttpSession session, Long itemId) {
        Map<Long, Integer> cart = getCart(session);
        cart.put(itemId, cart.getOrDefault(itemId, 0) + 1);
    }

    @Override
    public void removeFromCart(HttpSession session, Long itemId) {
        Map<Long, Integer> cart = getCart(session);
        cart.remove(itemId);
    }

    @Override
    public void changeCount(HttpSession session, Long itemId, String action) {
        Map<Long, Integer> cart = getCart(session);
        Integer currentCount = cart.get(itemId);

        if ("DELETE".equalsIgnoreCase(action)) {
            cart.remove(itemId);
        } else if ("PLUS".equalsIgnoreCase(action)) {
            int newCount = (currentCount != null ? currentCount : 0) + 1;
            cart.put(itemId, newCount);
        } else if ("MINUS".equalsIgnoreCase(action)) {
            if (currentCount == null || currentCount <= 0) {
                cart.remove(itemId);
            } else {
                cart.put(itemId, currentCount - 1);
            }
        }

    }


    @Override
    public Map<Long, Integer> getCartItems(HttpSession session) {
        return getCart(session);
    }

    @Override
    public int getCountInCart(HttpSession session, Long itemId) {
        return getCart(session).getOrDefault(itemId, 0);
    }

    @Override
    public long calculateTotalSum(HttpSession session) {
        Map<Long, Integer> cart = getCart(session);
        long total = 0;

        for (Map.Entry<Long, Integer> entry : cart.entrySet()) {
            Item item = itemRepository.findById(entry.getKey()).orElse(null);
            if (item != null) {
                total += item.getPrice() * entry.getValue();
            }
        }
        return total;
    }

    @Override
    public void clearCart(HttpSession session) {
        session.removeAttribute(CART_KEY);
    }
}
