package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Builder
public class BookingDto {

    final LocalDateTime start;
    final LocalDateTime end;
    final Item item;
    final User booker;
    final BookingStatus status;
}
