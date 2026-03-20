package ru.yandex.practicum.mymarket.cart;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.mymarket.item.model.Item;
import ru.yandex.practicum.mymarket.item.repository.ItemRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ItemRepository itemRepository;

    private MockHttpSession session;
    private Item savedItem;

    @BeforeEach
    void setUp() {
        session = new MockHttpSession();

        Item item = new Item();
        item.setTitle("Мяч");
        item.setDescription("Футбольный мяч");
        item.setImgPath("/images/ball.jpg");
        item.setPrice(1500L);
        savedItem = itemRepository.save(item);
    }

    @Test
    void getCartPage_emptyCart_returnsCartView() throws Exception {
        mockMvc.perform(get("/cart/items").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("cart"))
                .andExpect(model().attributeExists("items"))
                .andExpect(model().attributeExists("total"))
                .andExpect(model().attribute("total", 0L));
    }

    @Test
    void updateCart_plusItem_addsToCart() throws Exception {
        mockMvc.perform(post("/cart/items")
                        .param("id", savedItem.getId().toString())
                        .param("action", "PLUS")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("cart"));

        mockMvc.perform(get("/cart/items").session(session))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("items"));
    }

    @Test
    void updateCart_deleteItem_removesFromCart() throws Exception {
        mockMvc.perform(post("/cart/items")
                        .param("id", savedItem.getId().toString())
                        .param("action", "PLUS")
                        .session(session))
                .andExpect(status().isOk());

        mockMvc.perform(post("/cart/items")
                        .param("id", savedItem.getId().toString())
                        .param("action", "DELETE")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(model().attribute("total", 0L));
    }

    @Test
    void updateCart_plusThenMinus_decrements() throws Exception {
        mockMvc.perform(post("/cart/items")
                .param("id", savedItem.getId().toString())
                .param("action", "PLUS")
                .session(session));

        mockMvc.perform(post("/cart/items")
                .param("id", savedItem.getId().toString())
                .param("action", "PLUS")
                .session(session));

        mockMvc.perform(post("/cart/items")
                        .param("id", savedItem.getId().toString())
                        .param("action", "MINUS")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(model().attribute("total", 1500L));
    }
}
