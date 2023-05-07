package ru.practicum.shareit.request.model;

import lombok.Data;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
public class ItemRequest {

    long id;
    String description;
    User requestor;
    LocalDateTime createdTime;

}
