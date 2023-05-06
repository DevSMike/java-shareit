package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {

    ItemDto create(ItemDto item, int userId);

    ItemDto update(ItemDto item, int userId);

    ItemDto getItemById(int itemId, int userId);

    Collection<ItemDto> getItemsByUserId(int userId);

    Collection<ItemDto> getItemsBySearch(String text);

}
