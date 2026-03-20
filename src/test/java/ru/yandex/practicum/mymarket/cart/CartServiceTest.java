package ru.yandex.practicum.mymarket.cart;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpSession;
import ru.yandex.practicum.mymarket.cart.service.CartServiceImpl;
import ru.yandex.practicum.mymarket.item.model.Item;
import ru.yandex.practicum.mymarket.item.repository.ItemRepository;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private CartServiceImpl cartService;

    private MockHttpSession session;

    @BeforeEach
    void setUp() {
        session = new MockHttpSession();
    }

    @Test
    void getCartItems_emptySession_returnsEmptyMap() {
        Map<Long, Integer> cart = cartService.getCartItems(session);
        assertTrue(cart.isEmpty());
    }

    @Test
    void addToCart_firstTime_addsOneItem() {
        cartService.addToCart(session, 1L);

        Map<Long, Integer> cart = cartService.getCartItems(session);
        assertEquals(1, cart.get(1L));
        assertEquals(Map.of(1L, 1), cart);
    }

    @Test
    void addToCart_multipleTimes_incrementsCount() {
        cartService.addToCart(session, 1L);
        cartService.addToCart(session, 1L);

        assertEquals(2, cartService.getCartItems(session).get(1L));
    }

    @Test
    void changeCount_PLUS_newItem_addsOne() {
        cartService.changeCount(session, 1L, "PLUS");

        assertEquals(1, cartService.getCartItems(session).get(1L));
    }

    @Test
    void changeCount_PLUS_existingItem_increments() {
        cartService.changeCount(session, 1L, "PLUS");
        cartService.changeCount(session, 1L, "PLUS");

        assertEquals(2, cartService.getCartItems(session).get(1L));
    }


    @Test
    void changeCount_MINUS_fromTwo_becomesOne() {
        cartService.changeCount(session, 1L, "PLUS");
        cartService.changeCount(session, 1L, "PLUS");
        cartService.changeCount(session, 1L, "MINUS");

        assertEquals(1, cartService.getCartItems(session).get(1L));
    }

    @Test
    void changeCount_DELETE_removesItem() {
        cartService.changeCount(session, 1L, "PLUS");
        cartService.changeCount(session, 1L, "DELETE");

        assertNull(cartService.getCartItems(session).get(1L));
    }

    @Test
    void changeCount_DELETE_emptyCart_ignores() {
        cartService.changeCount(session, 1L, "DELETE");

        assertTrue(cartService.getCartItems(session).isEmpty());
    }

    @Test
    void getCountInCart_nonExistentItem_returnsZero() {
        int count = cartService.getCountInCart(session, 999L);
        assertEquals(0, count);
    }

    @Test
    void clearCart_removesAllItems() {
        cartService.changeCount(session, 1L, "PLUS");
        cartService.changeCount(session, 2L, "PLUS");

        cartService.clearCart(session);

        assertTrue(cartService.getCartItems(session).isEmpty());
    }

    @Test
    void calculateTotalSum_emptyCart_returnsZero() {
        long total = cartService.calculateTotalSum(session);
        assertEquals(0, total);
    }

    @Test
    void calculateTotalSum_withItems_calculatesCorrectly() {
        // Given
        Item item1 = mock(Item.class);
        when(item1.getPrice()).thenReturn(1500L);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item1));

        Item item2 = mock(Item.class);
        when(item2.getPrice()).thenReturn(500L);
        when(itemRepository.findById(2L)).thenReturn(Optional.of(item2));

        cartService.changeCount(session, 1L, "PLUS");
        cartService.changeCount(session, 1L, "PLUS");
        cartService.changeCount(session, 2L, "PLUS");
        cartService.changeCount(session, 2L, "PLUS");
        cartService.changeCount(session, 2L, "PLUS");


        long total = cartService.calculateTotalSum(session);


        assertEquals(4500, total);
        verify(itemRepository, times(2)).findById(anyLong());
    }

    @Test
    void calculateTotalSum_itemNotFound_ignores() {
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        cartService.changeCount(session, 1L, "PLUS");

        long total = cartService.calculateTotalSum(session);
        assertEquals(0, total);
    }
}
