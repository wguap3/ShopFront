package ru.yandex.practicum.mymarket.item.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemDto {
    private Long id;
    private String title;
    private String description;
    private String imgPath;
    private long price;
    private Integer count;


}
