package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

@Data
@Builder
public class Item {

    long id;
    String name;
    String description;
    Boolean available;
    ItemRequest request;
    User owner;

}
