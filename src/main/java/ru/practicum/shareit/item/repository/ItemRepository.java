package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemRepository {

    Item create(Item item, int userId);

    Item update(Item item, int userId);

    Item getItemById(int itemId);

    Collection<Item> getItemsByUserId(int userId);

    Collection<Item> getItemsBySearch(String text);
}
