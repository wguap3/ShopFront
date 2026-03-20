package ru.yandex.practicum.mymarket.item;

import jakarta.servlet.ServletException;
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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class ItemControllerTest {

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
    void getItemsPage_noParams_returnsItemsView() throws Exception {
        mockMvc.perform(get("/items").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("items"))
                .andExpect(model().attributeExists("items"))
                .andExpect(model().attributeExists("paging"))
                .andExpect(model().attributeExists("sort"));
    }

    @Test
    void getItemsPage_rootUrl_returnsItemsView() throws Exception {
        mockMvc.perform(get("/").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("items"));
    }

    @Test
    void getItemsPage_withSearch_returnsFilteredItems() throws Exception {
        mockMvc.perform(get("/items")
                        .param("search", "Мяч")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("items"))
                .andExpect(model().attributeExists("items"));
    }

    @Test
    void getItemsPage_withSortALPHA_returnsItems() throws Exception {
        mockMvc.perform(get("/items")
                        .param("sort", "ALPHA")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("items"))
                .andExpect(model().attribute("sort", "ALPHA"));
    }

    @Test
    void getItemsPage_withSortPRICE_returnsItems() throws Exception {
        mockMvc.perform(get("/items")
                        .param("sort", "PRICE")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("items"))
                .andExpect(model().attribute("sort", "PRICE"));
    }

    @Test
    void getItemsPage_withPagination_returnsCorrectPage() throws Exception {
        mockMvc.perform(get("/items")
                        .param("pageNumber", "1")
                        .param("pageSize", "2")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("items"))
                .andExpect(model().attributeExists("paging"));
    }

    @Test
    void getItemPage_existingItem_returnsItemView() throws Exception {
        mockMvc.perform(get("/items/{id}", savedItem.getId()).session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("item"))
                .andExpect(model().attributeExists("item"));
    }

    @Test
    void getItemPage_notExistingItem_throwsException() throws Exception {
        assertThrows(ServletException.class, () ->
                mockMvc.perform(get("/items/{id}", 9999L).session(session))
        );
    }

    @Test
    void updateCartFromItemsPage_plusAction_redirectsToItems() throws Exception {
        mockMvc.perform(post("/items")
                        .param("id", savedItem.getId().toString())
                        .param("action", "PLUS")
                        .session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/items*"));
    }

    @Test
    void updateCartFromItemsPage_withSearchAndSort_redirectsWithParams() throws Exception {
        mockMvc.perform(post("/items")
                        .param("id", savedItem.getId().toString())
                        .param("action", "PLUS")
                        .param("search", "Мяч")
                        .param("sort", "ALPHA")
                        .param("pageNumber", "1")
                        .param("pageSize", "5")
                        .session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/items*sort=ALPHA*"));
    }

    @Test
    void updateCartFromItemPage_plusAction_returnsItemView() throws Exception {
        mockMvc.perform(post("/items/{id}", savedItem.getId())
                        .param("action", "PLUS")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("item"))
                .andExpect(model().attributeExists("item"));
    }

    @Test
    void updateCartFromItemPage_minusAction_returnsItemView() throws Exception {
        mockMvc.perform(post("/items/{id}", savedItem.getId())
                        .param("action", "MINUS")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("item"))
                .andExpect(model().attributeExists("item"));
    }
}
