package ru.yandex.practicum.mymarket.orderItem.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemDto {
    private Long itemId;
    private String title;
    private String imgPath;
    private Integer count;
    private Long price;

    public Long getTotalPrice() {
        return price * count;
    }
}
