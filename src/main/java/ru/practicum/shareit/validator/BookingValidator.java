package ru.practicum.shareit.validator;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.IncorrectDataException;
import ru.practicum.shareit.exception.UnsupportedStatusException;


public class BookingValidator {


    public static void validateBookingState(String state) {
        try {
            BookingState.valueOf(state);
        } catch (Exception e) {
            throw new UnsupportedStatusException(state);
        }
    }

    public static void validateBookingId(long bookingId, BookingRepository repository) {
        if (bookingId < 0) {
            throw new IncorrectDataException("There is no booking with header-Id : " + bookingId);
        }
        repository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("There is no Booking with Id: " + bookingId));
    }

    public static Booking validateBookingIdAndReturns(long bookingId, BookingRepository repository) {
        if (bookingId < 0) {
            throw new IncorrectDataException("There is no booking with header-Id : " + bookingId);
        }
        return repository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("There is no Booking with Id: " + bookingId));
    }

}
