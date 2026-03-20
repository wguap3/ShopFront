package ru.yandex.practicum.mymarket.item.service;


import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.mymarket.cart.service.CartService;
import ru.yandex.practicum.mymarket.exception.ItemNotFoundException;
import ru.yandex.practicum.mymarket.item.dto.ItemDto;
import ru.yandex.practicum.mymarket.item.mapper.ItemMapper;
import ru.yandex.practicum.mymarket.item.model.Item;
import ru.yandex.practicum.mymarket.item.repository.ItemRepository;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final CartService cartService;

    @Override
    public Page<ItemDto> findItems(String search, String sort, Pageable pageable) {
        Pageable sortedPageable = applySort(pageable, sort);
        Page<Item> itemsPage;

        if (search != null && !search.isBlank()) {
            itemsPage = itemRepository.searchByTitleOrDescription(search.trim(), sortedPageable);
        } else {
            itemsPage = itemRepository.findAll(sortedPageable);
        }

        return itemsPage.map(itemMapper::toDto);
    }

    private Pageable applySort(Pageable pageable, String sort) {
        if ("ALPHA".equalsIgnoreCase(sort)) {
            return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("title").ascending());
        } else if ("PRICE".equalsIgnoreCase(sort)) {
            return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("price").ascending());
        }
        return pageable;
    }

    @Override
    public Item findById(Long id) {
        return itemRepository.findById(id).orElseThrow(() -> new RuntimeException("Item not found with id" + id));
    }

    @Override
    public List<ItemDto> convertItemsToCartDtos(Map<Long, Integer> cartItems) {
        if (cartItems.isEmpty()) {
            return List.of();
        }

        List<Item> items = itemRepository.findAllById(cartItems.keySet());

        Map<Long, Item> itemMap = items.stream()
                .collect(Collectors.toMap(Item::getId, Function.identity()));

        return cartItems.entrySet().stream()
                .map(entry -> {
                    Item item = itemMap.get(entry.getKey());
                    if (item == null) {
                        throw new ItemNotFoundException("Item not found: " + entry.getKey());
                    }
                    ItemDto dto = itemMapper.toDto(item);
                    dto.setCount(entry.getValue());
                    return dto;
                })
                .toList();
    }

    @Override
    public ItemDto findByIdWithCount(Long id, HttpSession session) {
        return itemRepository.findById(id)
                .map(item -> {
                    ItemDto dto = itemMapper.toDto(item);
                    dto.setCount(cartService.getCountInCart(session, id));
                    return dto;
                })
                .orElseThrow(() -> new RuntimeException("Item not found with id " + id));
    }

    @Override
    public int resolveCountInCart(Map<Long, Integer> cartItems, Long itemId) {
        return cartItems.getOrDefault(itemId, 0);
    }
}
