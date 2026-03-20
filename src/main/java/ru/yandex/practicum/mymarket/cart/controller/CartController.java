package ru.yandex.practicum.mymarket.cart.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.mymarket.cart.service.CartService;
import ru.yandex.practicum.mymarket.item.dto.ItemDto;
import ru.yandex.practicum.mymarket.item.service.ItemService;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;
    private final ItemService itemService;

    @GetMapping("/items")
    public String getCartPage(HttpSession session, Model model) {


        Map<Long, Integer> cartItems = cartService.getCartItems(session);


        List<ItemDto> items = itemService.convertItemsToCartDtos(cartItems);

        long total = cartService.calculateTotalSum(session);

        model.addAttribute("items", items);
        model.addAttribute("total", total);

        return "cart";
    }

    @PostMapping("/items")
    public String updateCartFromCartPage(
            @RequestParam Long id,
            @RequestParam String action,
            HttpSession session,
            Model model) {

        cartService.changeCount(session, id, action);

        Map<Long, Integer> cartItems = cartService.getCartItems(session);
        List<ItemDto> items = itemService.convertItemsToCartDtos(cartItems);
        long total = cartService.calculateTotalSum(session);

        model.addAttribute("items", items);
        model.addAttribute("total", total);

        return "cart";
    }

}

