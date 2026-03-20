package ru.yandex.practicum.mymarket.item.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.mymarket.cart.service.CartService;
import ru.yandex.practicum.mymarket.item.dto.ItemDto;
import ru.yandex.practicum.mymarket.item.service.ItemService;
import ru.yandex.practicum.mymarket.other.PagingInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final CartService cartService;

    @GetMapping({"/", "/items"})
    public String getItemsPage(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "NO") String sort,
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "5") int pageSize,
            HttpSession session,
            Model model) {

        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        Page<ItemDto> itemsPage = itemService.findItems(search, sort, pageable);

        Map<Long, Integer> cartItems = cartService.getCartItems(session);

        for (ItemDto dto : itemsPage.getContent()) {
            dto.setCount(itemService.resolveCountInCart(cartItems, dto.getId()));
        }

        List<List<ItemDto>> groupedItems = groupItemsByThree(itemsPage.getContent());

        PagingInfo paging = new PagingInfo(
                pageSize,
                pageNumber,
                itemsPage.hasPrevious(),
                itemsPage.hasNext()
        );

        model.addAttribute("items", groupedItems);
        model.addAttribute("search", search);
        model.addAttribute("sort", sort);
        model.addAttribute("paging", paging);

        return "items";
    }

    @PostMapping("/items")
    public String updateCartFromItemsPage(
            @RequestParam Long id,
            @RequestParam String action,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "NO") String sort,
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "5") int pageSize,
            HttpSession session) {

        cartService.changeCount(session, id, action);

        return String.format("redirect:/items?search=%s&sort=%s&pageNumber=%d&pageSize=%d",
                search != null ? search : "",
                sort,
                pageNumber,
                pageSize);
    }

    @GetMapping("/items/{id}")
    public String getItemPage(@PathVariable Long id, HttpSession session, Model model) {
        ItemDto item = itemService.findByIdWithCount(id, session);
        model.addAttribute("item", item);
        return "item";
    }

    @PostMapping("/items/{id}")
    public String updateCartFromItemPage(
            @PathVariable Long id,
            @RequestParam String action,
            HttpSession session,
            Model model) {

        cartService.changeCount(session, id, action);

        ItemDto item = itemService.findByIdWithCount(id, session);

        model.addAttribute("item", item);
        return "item";
    }

    private List<List<ItemDto>> groupItemsByThree(List<ItemDto> items) {
        List<List<ItemDto>> result = new ArrayList<>();
        for (int i = 0; i < items.size(); i += 3) {
            result.add(new ArrayList<>(
                    items.subList(i, Math.min(i + 3, items.size()))
            ));
        }
        if (result.isEmpty()) {
            result.add(new ArrayList<>());
        }
        return result;
    }

}
