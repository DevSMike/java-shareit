package ru.practicum.shareit.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.IncorrectDataException;
import ru.practicum.shareit.exception.UnsupportedStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingValidatorTest {

    @Mock
    BookingRepository bookingRepository;

    @InjectMocks
    BookingValidator bookingValidator;

    @Test
    void validateBookingState_whenStateIsIncorrect_thenThrowUnsupportedStatusException() {
        UnsupportedStatusException exception = assertThrows(UnsupportedStatusException.class,
                () -> bookingValidator.validateBookingState("NOT"));

        assertEquals(exception.getMessage(), "NOT");
    }

    @Test
    void validateBookingId_whenBookingNotExists_thenThrowEntityNotFoundException() {
        when(bookingRepository.findById(anyLong())).thenThrow(new EntityNotFoundException("Booking not found"));

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> bookingValidator.validateBookingId(1L));

        assertEquals(exception.getMessage(), "Booking not found");
    }

    @Test
    void validateBookingId_whenBookingIdLessThanZero_thenThrowIncorrectDataException() {
        IncorrectDataException exception = assertThrows(IncorrectDataException.class,
                () -> bookingValidator.validateBookingId(-1L));

        assertEquals(exception.getMessage(), "There is no booking with header-Id : -1");
    }

    @Test
    void validateBookingIdAndReturns_whenBookingNotFound_thenThrowEntityNotFoundException() {
        when(bookingRepository.findById(anyLong())).thenThrow(new EntityNotFoundException("Booking not found"));

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> bookingValidator.validateBookingIdAndReturns(1L));

        assertEquals(exception.getMessage(), "Booking not found");
    }

    @Test
    void validateBookingIdAndReturns_whenBookingExists_thenReturnBooking() {
        Booking booking = new Booking();
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        Booking actual =  bookingValidator.validateBookingIdAndReturns(1L);

        assertEquals(actual, booking);
    }
}