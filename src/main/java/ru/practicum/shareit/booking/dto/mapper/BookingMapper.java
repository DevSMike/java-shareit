package ru.practicum.shareit.booking.dto.mapper;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;

public class BookingMapper {

    public static BookingDto toBookingDto(Booking booking) {
        return BookingDto.builder()
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(booking.getItem())
                .status(booking.getStatus())
                .booker(booking.getBooker())
                .build();
    }
}
