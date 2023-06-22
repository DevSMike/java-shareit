package shareit.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import shareit.exception.IncorrectDataException;
import shareit.booking.BookingDto;
import shareit.booking.BookingState;
import shareit.exception.UnsupportedStatusException;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class BookingValidator {

    public void validateBookingState(String state) {
        String result = BookingState.checkState(state);
        if (result.isEmpty()) {
            throw new UnsupportedStatusException(state);
        }
    }

    public void validateBookingData(BookingDto bookingDto) {
        if (bookingDto.getStart() == null || bookingDto.getEnd() == null) {
            throw new IncorrectDataException("Booking: Dates are null!");
        }
        if (bookingDto.getEnd().isBefore(bookingDto.getStart()) || bookingDto.getStart().isEqual(bookingDto.getEnd())
                || bookingDto.getEnd().isBefore(LocalDateTime.now()) || bookingDto.getStart().isBefore(LocalDateTime.now())) {
            throw new IncorrectDataException("Booking: Problem in dates");
        }
    }
}
