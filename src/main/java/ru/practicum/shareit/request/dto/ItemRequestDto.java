package ru.practicum.shareit.request.dto;


import lombok.Builder;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Builder
public class ItemRequestDto {

    private final String description;
    private final User requestor;
    private final LocalDateTime createdTime;
}
