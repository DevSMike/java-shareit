package ru.practicum.shareit.request.dto;


import lombok.Builder;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Builder
public class ItemRequestDto {

    final String description;
    final User requestor;
    final LocalDateTime createdTime;
}
