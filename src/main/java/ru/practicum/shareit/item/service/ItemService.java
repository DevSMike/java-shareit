package ru.practicum.shareit.item.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.comment.CommentDto;

import java.util.Collection;

public interface ItemService {

    ItemDto create(ItemDto item, long userId);

    ItemDto update(ItemDto item, long userId);

    ItemDto getItemById(long itemId, long userId);

    Collection<ItemDto> getItemsByUserId(long userId, Pageable page);

    Collection<ItemDto> getItemsBySearch(String text, Pageable page);

    ItemDto checkItemOwner(Long itemId, Long ownerId);

    CommentDto addCommentToItem(Long userId, Long itemId, CommentDto commentDto);
}
