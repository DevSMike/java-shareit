package ru.practicum.shareit.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.IncorrectDataException;
import ru.practicum.shareit.exception.UnsupportedStatusException;

@Component
@RequiredArgsConstructor
public class BookingValidator {

    private final BookingRepository repository;

    public void validateBookingState(String state) {
        try {
            BookingState.valueOf(state);
        } catch (Exception e) {
            throw new UnsupportedStatusException(state);
        }
    }

    public void validateBookingId(long bookingId) {
        if (bookingId < 0) {
            throw new IncorrectDataException("There is no booking with header-Id : " + bookingId);
        }
        repository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("There is no Booking with Id: " + bookingId));
    }

}
