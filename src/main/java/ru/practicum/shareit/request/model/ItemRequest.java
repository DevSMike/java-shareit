package ru.practicum.shareit.request.model;

import lombok.Data;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
public class ItemRequest {

    private long id;
    private String description;
    private User requestor;
    private LocalDateTime createdTime;

}
