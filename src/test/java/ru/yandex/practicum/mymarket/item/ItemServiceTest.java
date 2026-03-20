package ru.yandex.practicum.mymarket.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockHttpSession;
import ru.yandex.practicum.mymarket.cart.service.CartService;
import ru.yandex.practicum.mymarket.item.dto.ItemDto;
import ru.yandex.practicum.mymarket.item.mapper.ItemMapper;
import ru.yandex.practicum.mymarket.item.model.Item;
import ru.yandex.practicum.mymarket.item.repository.ItemRepository;
import ru.yandex.practicum.mymarket.item.service.ItemServiceImpl;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ItemMapper itemMapper;

    @Mock
    private CartService cartService;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    void findItems_noSearch_noSort_returnsAll() {
        Pageable pageable = PageRequest.of(0, 5);
        Item item = new Item();
        ItemDto itemDto = new ItemDto();
        Page<Item> itemsPage = new PageImpl<>(List.of(item));
        when(itemRepository.findAll(pageable)).thenReturn(itemsPage);
        when(itemMapper.toDto(item)).thenReturn(itemDto);

        Page<ItemDto> result = itemService.findItems(null, "NO", pageable);

        assertEquals(1, result.getTotalElements());
        verify(itemRepository).findAll(pageable);
        verify(itemRepository, never()).searchByTitleOrDescription(any(), any());
    }

    @Test
    void findItems_blankSearch_returnsAll() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<Item> itemsPage = new PageImpl<>(List.of(new Item()));
        when(itemRepository.findAll(pageable)).thenReturn(itemsPage);
        when(itemMapper.toDto(any())).thenReturn(new ItemDto());

        Page<ItemDto> result = itemService.findItems("   ", "NO", pageable);

        verify(itemRepository).findAll(pageable);
        verify(itemRepository, never()).searchByTitleOrDescription(any(), any());
    }

    @Test
    void findItems_withSearch_callsSearchRepository() {
        Pageable pageable = PageRequest.of(0, 5);
        Item item = new Item();
        Page<Item> itemsPage = new PageImpl<>(List.of(item));
        when(itemRepository.searchByTitleOrDescription("мяч", pageable)).thenReturn(itemsPage);
        when(itemMapper.toDto(item)).thenReturn(new ItemDto());

        itemService.findItems("мяч", "NO", pageable);

        verify(itemRepository).searchByTitleOrDescription("мяч", pageable);
        verify(itemRepository, never()).findAll(any(Pageable.class));
    }

    @Test
    void findItems_sortALPHA_appliesTitleSort() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<Item> emptyPage = new PageImpl<>(List.of());
        when(itemRepository.findAll(any(Pageable.class))).thenReturn(emptyPage);

        itemService.findItems(null, "ALPHA", pageable);

        verify(itemRepository).findAll(argThat((Pageable p) ->
                p.getSort().getOrderFor("title") != null
        ));
    }

    @Test
    void findItems_sortPRICE_appliesPriceSort() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<Item> emptyPage = new PageImpl<>(List.of());
        when(itemRepository.findAll(any(Pageable.class))).thenReturn(emptyPage);

        itemService.findItems(null, "PRICE", pageable);

        verify(itemRepository).findAll(argThat((Pageable p) ->
                p.getSort().getOrderFor("price") != null
        ));
    }

    @Test
    void findById_exists_returnsItem() {
        Item item = new Item();
        item.setId(1L);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        Item result = itemService.findById(1L);

        assertEquals(1L, result.getId());
    }

    @Test
    void findById_notExists_throwsException() {
        when(itemRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> itemService.findById(99L));
    }

    @Test
    void convertItemsToCartDtos_emptyCart_returnsEmptyList() {
        List<ItemDto> result = itemService.convertItemsToCartDtos(Map.of());

        assertTrue(result.isEmpty());
        verify(itemRepository, never()).findAllById(any());
    }

    @Test
    void convertItemsToCartDtos_withItems_returnsDtosWithCount() {
        Item item = new Item();
        item.setId(1L);
        ItemDto dto = new ItemDto();
        when(itemRepository.findAllById(any())).thenReturn(List.of(item));
        when(itemMapper.toDto(item)).thenReturn(dto);

        List<ItemDto> result = itemService.convertItemsToCartDtos(Map.of(1L, 3));

        assertEquals(1, result.size());
        assertEquals(3, result.get(0).getCount());
    }

    @Test
    void convertItemsToCartDtos_itemNotFound_throwsException() {
        when(itemRepository.findAllById(any())).thenReturn(List.of());

        assertThrows(RuntimeException.class,
                () -> itemService.convertItemsToCartDtos(Map.of(99L, 1)));
    }

    @Test
    void findByIdWithCount_exists_returnsDtoWithCount() {
        MockHttpSession session = new MockHttpSession();
        Item item = new Item();
        item.setId(1L);
        ItemDto dto = new ItemDto();
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(itemMapper.toDto(item)).thenReturn(dto);
        when(cartService.getCountInCart(session, 1L)).thenReturn(2);

        ItemDto result = itemService.findByIdWithCount(1L, session);

        assertEquals(2, result.getCount());
    }

    @Test
    void findByIdWithCount_notFound_throwsException() {
        MockHttpSession session = new MockHttpSession();
        when(itemRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> itemService.findByIdWithCount(99L, session));
    }

    @Test
    void resolveCountInCart_itemInCart_returnsCount() {
        Map<Long, Integer> cart = Map.of(1L, 5);

        int count = itemService.resolveCountInCart(cart, 1L);

        assertEquals(5, count);
    }

    @Test
    void resolveCountInCart_itemNotInCart_returnsZero() {
        Map<Long, Integer> cart = Map.of();

        int count = itemService.resolveCountInCart(cart, 99L);

        assertEquals(0, count);
    }
}
