package ru.practicum.shareit.item.repository.inmemmory;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemRepository {

    Item create(Item item, long userId);

    Item update(Item item, long userId);

    Item getItemById(long itemId);

    Collection<Item> getItemsByUserId(long userId);

    Collection<Item> getItemsBySearch(String text);
}
