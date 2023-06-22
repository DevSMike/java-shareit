package ru.practicum.shareit.booking.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {

    BookingDto addBooking(BookingDto bookingDto, Long bookerId);

    BookingDto approveBooking(Long bookingId, Long ownerId, String approve);

    BookingDto getBookingInfo(Long bookingId, Long userId);

    List<BookingDto> getAllBookingsByUserId(Long userId, String state, Pageable page);

    List<BookingDto> getAllBookingsByOwnerId(Long ownerId, String state, Pageable page);
}
