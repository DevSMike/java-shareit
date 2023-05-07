package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.request.model.ItemRequest;

@Builder
@Data
public class ItemDto {

    Long id;
    final String name;
    final String description;
    final Boolean available;
    final ItemRequest request;
}
