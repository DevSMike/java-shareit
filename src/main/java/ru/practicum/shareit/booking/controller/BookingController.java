package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@Slf4j
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDto createBooking(@RequestBody BookingDto bookingDto, HttpServletRequest request) {
        log.info("Creating a booking : {}", bookingDto);
        return bookingService.addBooking(bookingDto, Long.valueOf(request.getHeader("X-Sharer-User-Id")));
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveBooking(@PathVariable Long bookingId, @RequestParam String approved, HttpServletRequest request) {
        log.info("Make approve status to booking: {}, status: {}", bookingId, approved);
        return bookingService.approveBooking(bookingId, Long.valueOf(request.getHeader("X-Sharer-User-Id")), approved);
    }

    @GetMapping
    public List<BookingDto> getAllBookingsForUser(@RequestParam(defaultValue = "ALL") String state, HttpServletRequest request) {
        log.info("Getting info by user bookings");
        return bookingService.getAllBookingsByUserId(Long.valueOf(request.getHeader("X-Sharer-User-Id")), state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllBookingsForOwner(@RequestParam(defaultValue = "ALL") String state, HttpServletRequest request) {
        log.info("Getting info by owner bookings");
        return bookingService.getAllBookingsByOwnerId(Long.valueOf(request.getHeader("X-Sharer-User-Id")), state);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getInfoForBooking(@PathVariable Long bookingId, HttpServletRequest request) {
        log.info("Getting info for booking: {}", bookingId);
        return bookingService.getBookingInfo(bookingId, Long.valueOf(request.getHeader("X-Sharer-User-Id")));
    }
}
