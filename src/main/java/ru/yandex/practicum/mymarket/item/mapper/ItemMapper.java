package ru.yandex.practicum.mymarket.item.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.mymarket.item.dto.ItemDto;
import ru.yandex.practicum.mymarket.item.model.Item;

@Mapper(componentModel = "spring")
public interface ItemMapper {
    ItemDto toDto(Item item);

    Item toEntity(ItemDto dto);
}
