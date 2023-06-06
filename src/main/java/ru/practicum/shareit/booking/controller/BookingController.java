package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.validator.PageableValidator;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@Slf4j
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;
    private final PageableValidator pageableValidator;

    @PostMapping
    public BookingDto createBooking(@RequestBody BookingDto bookingDto, HttpServletRequest request) {
        log.info("Creating a booking : {}", bookingDto);
        return bookingService.addBooking(bookingDto, (long)(request.getIntHeader("X-Sharer-User-Id")));
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveBooking(@PathVariable Long bookingId, @RequestParam String approved, HttpServletRequest request) {
        log.info("Make approve status to booking: {}, status: {}", bookingId, approved);
        return bookingService.approveBooking(bookingId, (long)(request.getIntHeader("X-Sharer-User-Id")), approved);
    }

    @GetMapping
    public List<BookingDto> getAllBookingsForUser(@RequestParam(defaultValue = "ALL") String state,
                                                  @RequestParam(defaultValue = "0") Integer from,
                                                  @RequestParam(defaultValue = "10") Integer size,
                                                  HttpServletRequest request) {
        pageableValidator.checkingPageableParams(from, size);
        log.info("Getting info by user bookings");
        Pageable page = PageRequest.of(from / size, size);
        return bookingService.getAllBookingsByUserId((long) request.getIntHeader("X-Sharer-User-Id"), state, page);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllBookingsForOwner(@RequestParam(defaultValue = "ALL") String state,
                                                   @RequestParam(defaultValue = "0") Integer from,
                                                   @RequestParam(defaultValue = "10") Integer size,
                                                   HttpServletRequest request) {
        pageableValidator.checkingPageableParams(from, size);
        log.info("Getting info by owner bookings");
        Pageable page = PageRequest.of(from / size, size);
        return bookingService.getAllBookingsByOwnerId((long) request.getIntHeader("X-Sharer-User-Id"), state, page);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getInfoForBooking(@PathVariable Long bookingId, HttpServletRequest request) {
        log.info("Getting info for booking: {}", bookingId);
        return bookingService.getBookingInfo(bookingId, (long) request.getIntHeader("X-Sharer-User-Id"));
    }
}
