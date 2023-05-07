package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {

    ItemDto create(ItemDto item, long userId);

    ItemDto update(ItemDto item, long userId);

    ItemDto getItemById(long itemId, long userId);

    Collection<ItemDto> getItemsByUserId(long userId);

    Collection<ItemDto> getItemsBySearch(String text);

}
