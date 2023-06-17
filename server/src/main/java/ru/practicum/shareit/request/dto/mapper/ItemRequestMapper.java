package ru.practicum.shareit.request.dto.mapper;


import ru.practicum.shareit.item.dto.RequestItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.user.dto.mapper.UserMapper.toUserDto;

public class ItemRequestMapper {

    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto, User requester) {
        return ItemRequest.builder()
                .description(itemRequestDto.getDescription())
                .creationDate(itemRequestDto.getCreated())
                .requester(requester)
                .build();
    }

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        List<RequestItemDto> items = null;
        if (itemRequest.getResponsesToRequest() != null) {
            items = itemRequest.getResponsesToRequest().stream()
                    .map(ItemRequestMapper::makeResultItemDto)
                    .collect(Collectors.toList());
        }

        ItemRequestDto requestDto = ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreationDate())
                .items(items)
                .build();

        if (itemRequest.getRequester() != null) {
            UserDto requester = toUserDto(itemRequest.getRequester());
            requestDto.setRequester(requester);
        }

        return requestDto;
    }

    public static RequestItemDto makeResultItemDto(Item item) {
        return RequestItemDto.builder()
                .name(item.getName())
                .ownerId(item.ownerId())
                .id(item.getId())
                .requestId(item.getRequest().getId())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }
}
