package ru.yandex.practicum.mymarket.order;

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
import ru.yandex.practicum.mymarket.order.repository.OrderRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private OrderRepository orderRepository;

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
    void getOrdersPage_noOrders_returnsOrdersView() throws Exception {
        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(view().name("orders"))
                .andExpect(model().attributeExists("orders"));
    }

    @Test
    void getOrderPage_existingOrder_returnsOrderView() throws Exception {
        mockMvc.perform(post("/cart/items")
                .param("id", savedItem.getId().toString())
                .param("action", "PLUS")
                .session(session));

        String redirectUrl = mockMvc.perform(post("/orders/buy").session(session))
                .andExpect(status().is3xxRedirection())
                .andReturn()
                .getResponse()
                .getRedirectedUrl();

        mockMvc.perform(get(redirectUrl).session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("order"))
                .andExpect(model().attributeExists("order"))
                .andExpect(model().attribute("newOrder", true));
    }


    @Test
    void placeOrder_withItemsInCart_createsOrderAndRedirects() throws Exception {
        mockMvc.perform(post("/cart/items")
                .param("id", savedItem.getId().toString())
                .param("action", "PLUS")
                .session(session));

        mockMvc.perform(post("/orders/buy").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/orders/*?newOrder=true"));
    }

    @Test
    void placeOrder_withItemsInCart_clearsCart() throws Exception {
        mockMvc.perform(post("/cart/items")
                .param("id", savedItem.getId().toString())
                .param("action", "PLUS")
                .session(session));

        mockMvc.perform(post("/orders/buy").session(session));

        mockMvc.perform(get("/cart/items").session(session))
                .andExpect(status().isOk())
                .andExpect(model().attribute("total", 0L));
    }


    @Test
    void placeOrder_savesOrderToDatabase() throws Exception {
        mockMvc.perform(post("/cart/items")
                .param("id", savedItem.getId().toString())
                .param("action", "PLUS")
                .session(session));

        mockMvc.perform(post("/orders/buy").session(session));

        long count = orderRepository.count();
        assertEquals(1, count);
    }

    @Test
    void getOrdersPage_afterPlaceOrder_showsOrder() throws Exception {
        mockMvc.perform(post("/cart/items")
                .param("id", savedItem.getId().toString())
                .param("action", "PLUS")
                .session(session));

        mockMvc.perform(post("/orders/buy").session(session));

        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(view().name("orders"))
                .andExpect(model().attributeExists("orders"));
    }
}
