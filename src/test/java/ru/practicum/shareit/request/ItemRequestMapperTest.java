package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ItemRequestMapperTest {

    @Test
    void toItemRequest() {
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .created(LocalDateTime.now())
                .description("text")
                .build();
        User requester = User.builder()
                .id(1L)
                .build();

        ItemRequest actual = ItemRequestMapper.toItemRequest(itemRequestDto, requester);

        assertEquals(itemRequestDto.getDescription(), actual.getDescription());
        assertEquals(actual.getRequester().getId(), requester.getId());
    }

    @Test
    void toItemRequestDto() {
        User owner = User.builder()
                .id(1L)
                .build();
        ItemRequest requestToItem = ItemRequest.builder()
                .creationDate(LocalDateTime.now())
                .id(2L)
                .description("desc")
                .build();
        Item item = Item.builder()
                .name("name")
                .owner(owner)
                .request(requestToItem)
                .description("desc")
                .available(true)
                .build();
        ItemRequest request = ItemRequest.builder()
                .creationDate(LocalDateTime.now())
                .id(1L)
                .description("desc")
                .responsesToRequest(List.of(item))
                .build();

        ItemRequestDto actual = ItemRequestMapper.toItemRequestDto(request);

        assertEquals(actual.getDescription(), request.getDescription());
        assertEquals(actual.getItems().size(), 1);
    }
}