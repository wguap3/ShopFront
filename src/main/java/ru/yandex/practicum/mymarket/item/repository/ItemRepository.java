package ru.yandex.practicum.mymarket.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.yandex.practicum.mymarket.item.model.Item;

public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query("SELECT i FROM Item i WHERE LOWER(i.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(i.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Item> searchByTitleOrDescription(@Param("keyword") String keyword, Pageable pageable);
}
