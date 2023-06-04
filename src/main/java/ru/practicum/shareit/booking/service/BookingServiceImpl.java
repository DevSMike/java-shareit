package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.IncorrectDataException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.dto.mapper.BookingMapper.*;
import static ru.practicum.shareit.validator.BookingValidator.validateBookingIdAndReturns;
import static ru.practicum.shareit.validator.BookingValidator.validateBookingState;
import static ru.practicum.shareit.validator.ItemValidator.validateItemIdAndReturns;
import static ru.practicum.shareit.validator.UserValidator.validateUserId;
import static ru.practicum.shareit.validator.UserValidator.validateUserIdAndReturn;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private static final Sort SORT_BY_START_DESC = Sort.by("start").descending();
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;

    @Override
    public BookingDto addBooking(BookingDto bookingDto, Long bookerId) {
        User booker = validateUserIdAndReturn(bookerId, userRepository);
        Item itemFromDb = validateItemIdAndReturns(bookingDto.getItemId(), itemRepository);

        if (itemFromDb.getOwner().getId() == bookerId) {
            throw new EntityNotFoundException("Owner can't book his item");
        }
        if (!itemFromDb.getAvailable()) {
            throw new IncorrectDataException("Booking: Item is unavailable");
        }
        if (bookingDto.getStart() == null || bookingDto.getEnd() == null) {
            throw new IncorrectDataException("Booking: Dates are null!");
        }
        if (bookingDto.getEnd().isBefore(bookingDto.getStart()) || bookingDto.getStart().isEqual(bookingDto.getEnd())
                || bookingDto.getEnd().isBefore(LocalDateTime.now()) || bookingDto.getStart().isBefore(LocalDateTime.now())) {
            throw new IncorrectDataException("Booking: Problem in dates");
        }
        bookingDto.setStatus(BookingStatus.WAITING);
        return toBookingDto(bookingRepository.save(toBookingDb(bookingDto, itemFromDb, booker)));
    }

    @Override
    public BookingDto approveBooking(Long bookingId, Long ownerId, String approve) {
        validateUserId(ownerId, userRepository);
        Booking bookingFromDb = validateBookingIdAndReturns(bookingId, bookingRepository);

        BookingDto bookingDto = toBookingDto(bookingFromDb);
        if (!Objects.equals(bookingDto.getItem().getOwnerId(), ownerId)) {
            throw new EntityNotFoundException("User with id = " + ownerId + " is not an owner!");
        }

        switch (approve.toLowerCase()) {
            case "true": {
                if (bookingDto.getStatus().equals(BookingStatus.APPROVED)) {
                    throw new IncorrectDataException("Status is Approved");
                }
                bookingDto.setStatus(BookingStatus.APPROVED);
                break;
            }
            case "false": {
                bookingDto.setStatus(BookingStatus.REJECTED);
                break;
            }
            default:
                throw new IncorrectDataException("Incorrect data in approve method");
        }
        Booking bookingToUpdate = toBookingUpdate(bookingDto, bookingFromDb);
        bookingRepository.save(bookingToUpdate);
        return toBookingDto(bookingToUpdate);
    }

    @Override
    public BookingDto getBookingInfo(Long bookingId, Long userId) {
        validateUserId(userId, userRepository);
        BookingDto bookingDto = toBookingDto(validateBookingIdAndReturns(bookingId, bookingRepository));
        if (!Objects.equals(bookingDto.getItem().getOwnerId(), userId) && !Objects.equals(bookingDto.getBooker().getId(), userId)) {
            throw new EntityNotFoundException("User with id = " + userId + " is not an owner!");
        }
        return bookingDto;
    }

    @Override
    public List<BookingDto> getAllBookingsByUserId(Long userId, String state, Pageable page) {
        validateUserId(userId, userRepository);
        validateBookingState(state);

        Pageable pageForBookings = PageRequest.of(page.getPageNumber(), page.getPageSize(), SORT_BY_START_DESC);
        List<Booking> bookings;
        switch (state.toUpperCase()) {
            case "WAITING": {
                bookings = new ArrayList<>(bookingRepository.findAllByBookerIdAndWaitingStatus(userId, BookingStatus.WAITING, pageForBookings));
                break;
            }
            case "REJECTED": {
                bookings = new ArrayList<>(bookingRepository.findAllByBookerIdAndRejectedStatus(userId, List.of(BookingStatus.REJECTED, BookingStatus.CANCELED), pageForBookings));
                break;
            }
            case "CURRENT": {
                bookings = new ArrayList<>(bookingRepository.findAllByBookerIdAndCurrentStatus(userId, LocalDateTime.now(), pageForBookings));
                break;
            }
            case "FUTURE": {
                bookings = new ArrayList<>(bookingRepository.findAllByBookerIdAndFutureStatus(userId, LocalDateTime.now(), pageForBookings));
                break;
            }
            case "PAST": {
                bookings = new ArrayList<>(bookingRepository.findAllByBookerIdAndPastStatus(userId, LocalDateTime.now(), pageForBookings));
                break;
            }
            case "ALL": {
                bookings = new ArrayList<>(bookingRepository.findAllByBooker_Id(userId, pageForBookings));
                break;
            }
            default:
                bookings = new ArrayList<>();
        }
        return bookings.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getAllBookingsByOwnerId(Long ownerId, String state, Pageable page) {
        validateUserId(ownerId, userRepository);
        validateBookingState(state);
        Pageable pageForBookings = PageRequest.of(page.getPageNumber(), page.getPageSize(), SORT_BY_START_DESC);

        List<Long> userItemsIds = itemRepository.findByOwner_Id_WithoutPageable(ownerId).stream()
                .map(Item::getId)
                .collect(Collectors.toList());
        if (userItemsIds.isEmpty()) {
            throw new IncorrectDataException("This method only for users who have >1 items");
        }
        List<Booking> bookings;
        switch (state.toUpperCase()) {
            case "WAITING": {
                bookings = new ArrayList<>(bookingRepository.findAllByOwnerItemsAndWaitingStatus(userItemsIds, BookingStatus.WAITING, pageForBookings));
                break;
            }
            case "REJECTED": {
                bookings = new ArrayList<>(bookingRepository.findAllByOwnerItemsAndRejectedStatus(userItemsIds, List.of(BookingStatus.REJECTED, BookingStatus.CANCELED), pageForBookings));
                break;
            }
            case "CURRENT": {
                bookings = new ArrayList<>(bookingRepository.findAllByOwnerItemsAndCurrentStatus(userItemsIds, LocalDateTime.now(), pageForBookings));
                break;
            }
            case "FUTURE": {
                bookings = new ArrayList<>(bookingRepository.findAllByOwnerItemsAndFutureStatus(userItemsIds, LocalDateTime.now(), pageForBookings));
                break;
            }
            case "PAST": {
                bookings = new ArrayList<>(bookingRepository.findAllByOwnerItemsAndPastStatus(userItemsIds, LocalDateTime.now(), pageForBookings));
                break;
            }
            case "ALL": {
                bookings = new ArrayList<>(bookingRepository.findAllByOwnerItems(userItemsIds, pageForBookings));
                break;
            }
            default:
                bookings = new ArrayList<>();
        }
        return bookings.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }
}
