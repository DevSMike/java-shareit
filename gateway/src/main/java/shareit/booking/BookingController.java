package shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import shareit.exception.IncorrectDataException;
import shareit.validator.BookingValidator;
import shareit.validator.PageableValidator;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {

    private final BookingClient bookingClient;
    private final PageableValidator pageableValidator;
    private final BookingValidator bookingValidator;

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestBody BookingDto bookingDto, @RequestHeader("X-Sharer-User-Id") @Positive long userId) {
        bookingValidator.validateBookingData(bookingDto);
        log.debug("Gateway: Creating a booking : {}", bookingDto);
        return bookingClient.createBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(@PathVariable @Positive Long bookingId, @RequestParam @NotNull String approved, @RequestHeader("X-Sharer-User-Id") @Positive long userId) {
        if (!approved.equals("true") && !approved.equals("false")) {
            throw new IncorrectDataException("Approved can be only TRUE or FALSE");
        }
        log.debug("Gateway: Make approve status to booking: {}, status: {}", bookingId, approved);
        return bookingClient.approveBooking(userId, bookingId, approved);
    }

    @GetMapping
    public ResponseEntity<Object> getAllBookingsForUser(@RequestParam(defaultValue = "ALL") String state,
                                                        @RequestParam(defaultValue = "0") Integer from,
                                                        @RequestParam(defaultValue = "10") Integer size,
                                                        @RequestHeader("X-Sharer-User-Id") @Positive long userId) {
        pageableValidator.checkingPageableParams(from, size);
        bookingValidator.validateBookingState(state);
        log.debug("Gateway: Getting info by user bookings");
        return bookingClient.getAllBookingsForUser(userId, from, size, state);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllBookingsForOwner(@RequestParam(defaultValue = "ALL") String state,
                                                         @RequestParam(defaultValue = "0") Integer from,
                                                         @RequestParam(defaultValue = "10") Integer size,
                                                         @RequestHeader("X-Sharer-User-Id") @Positive long userId) {
        pageableValidator.checkingPageableParams(from, size);
        bookingValidator.validateBookingState(state);
        log.debug("Gateway: Getting info by owner bookings");
        return bookingClient.getAllBookingsForOwner(userId, from, size, state);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getInfoForBooking(@PathVariable @Positive Long bookingId, @RequestHeader("X-Sharer-User-Id") @Positive long userId) {
        log.debug("Gateway: Getting info for booking: {}", bookingId);
        return bookingClient.getInfoForBooking(userId, bookingId);
    }
}
