package ru.yandex.practicum.mymarket.other;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagingInfo {
    private int pageSize;
    private int pageNumber;
    private boolean hasPrevious;
    private boolean hasNext;


}