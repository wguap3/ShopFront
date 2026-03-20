package ru.yandex.practicum.mymarket.item.service;

import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.yandex.practicum.mymarket.item.dto.ItemDto;
import ru.yandex.practicum.mymarket.item.model.Item;

import java.util.List;
import java.util.Map;

public interface ItemService {
    Page<ItemDto> findItems(String search, String sort, Pageable pageable);

    Item findById(Long id);

    List<ItemDto> convertItemsToCartDtos(Map<Long, Integer> cartItems);

    int resolveCountInCart(Map<Long, Integer> cartItems, Long itemId);

    ItemDto findByIdWithCount(Long id, HttpSession session);
}
