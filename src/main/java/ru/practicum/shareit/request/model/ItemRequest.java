package ru.practicum.shareit.request.model;

import lombok.Data;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
public class ItemRequest {

    private int id;
    private final String description;
    private final User requestor;
    private final LocalDateTime createdTime;

}
