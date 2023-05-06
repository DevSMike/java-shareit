package ru.practicum.shareit.request.dto.mapper;


import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;

public class ItemRequestMapper {

    public static ItemRequestDto itemRequestDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .requestor(itemRequest.getRequestor())
                .createdTime(itemRequest.getCreatedTime())
                .description(itemRequest.getDescription())
                .build();
    }
}
