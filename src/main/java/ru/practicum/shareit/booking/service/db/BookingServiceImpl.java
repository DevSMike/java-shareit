package ru.practicum.shareit.booking.service.db;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepositoryDb;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.IncorrectDataException;
import ru.practicum.shareit.exception.UnsupportedStatusException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.dto.mapper.BookingMapper.*;
import static ru.practicum.shareit.item.dto.mapper.ItemMapper.toItem;
import static ru.practicum.shareit.user.dto.mapper.UserMapper.toUser;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final UserService userService;
    private final ItemService itemService;
    private final BookingRepositoryDb bookingRepository;

    @Override
    public BookingDto addBooking(BookingDto bookingDto, Long bookerId) {
        UserDto userFromDb = checkingUserId(bookerId);
        ItemDto itemFromDb = itemService.getItemById(bookingDto.getItemId(), bookerId);
        if (itemFromDb.getOwnerId().equals(bookerId)) {
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
        return toBookingDto(bookingRepository.save(toBookingDb(bookingDto, toItem(itemFromDb), toUser(userFromDb))));
    }

    @Override
    public BookingDto approveBooking(Long bookingId, Long ownerId, String approve) {
        BookingDto bookingDto = toBookingDto(bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("There is no Booking with Id: " + bookingId)));
        checkingUserId(ownerId);
        ItemDto owner = itemService.checkItemOwner(bookingDto.getItem().getId(), ownerId);
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
        Booking updateBooking = toBookingUpdate(bookingDto, bookingRepository.findById(bookingId).get());
        bookingRepository.updateStatus(updateBooking.getStatus(), updateBooking.getId());
        return toBookingDto(updateBooking);
    }

    @Override
    public BookingDto getBookingInfo(Long bookingId, Long userId) {
        checkingUserId(userId);
        BookingDto bookingDto = toBookingDto(bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("There is no Booking with Id: " + bookingId)));
        try {
            itemService.checkItemOwner(bookingDto.getItem().getId(), userId);
        } catch (EntityNotFoundException e) {
            if (!bookingDto.getBooker().getId().equals(userId)) {
                throw new EntityNotFoundException("User cant get info on this booking");
            }
        }
        return bookingDto;
    }

    @Override
    public List<BookingDto> getAllBookingsByUserId(Long userId, String state) {
        checkingUserId(userId);
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        switch (state.toUpperCase()) {
            case "WAITING": {
                return bookingRepository.findAllByBookerIdAndWaitingStatus(userId, BookingStatus.WAITING, sort)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            }
            case "REJECTED": {
                return bookingRepository.findAllByBookerIdAndRejectedStatus(userId, List.of(BookingStatus.REJECTED, BookingStatus.CANCELED), sort)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            }
            case "CURRENT": {
                return bookingRepository.findAllByBookerIdAndCurrentStatus(userId, LocalDateTime.now(), sort)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());

            }
            case "FUTURE": {
                return bookingRepository.findAllByBookerIdAndFutureStatus(userId, LocalDateTime.now(), sort)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());

            }
            case "PAST": {
                return bookingRepository.findAllByBookerIdAndPastStatus(userId, LocalDateTime.now(), sort)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            }
            case "ALL": {
                return bookingRepository.findAllByBooker_Id(userId, sort)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            }
            default:
                throw new UnsupportedStatusException(state);
        }
    }

    @Override
    public List<BookingDto> getAllBookingsByOwnerId(Long ownerId, String state) {
        List<Long> userItemsIds = itemService.getItemsByUserId(ownerId)
                .stream()
                .map(ItemDto::getId)
                .collect(Collectors.toList());
        if (userItemsIds.isEmpty()) {
            throw new IncorrectDataException("This method only for users who have >1 items");
        }
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        switch (state.toUpperCase()) {
            case "WAITING": {
                return bookingRepository.findAllByOwnerItemsAndWaitingStatus(userItemsIds, BookingStatus.WAITING, sort)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            }
            case "REJECTED": {
                return bookingRepository.findAllByOwnerItemsAndRejectedStatus(userItemsIds, List.of(BookingStatus.REJECTED, BookingStatus.CANCELED), sort)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            }
            case "CURRENT": {
                return bookingRepository.findAllByOwnerItemsAndCurrentStatus(userItemsIds, LocalDateTime.now(), sort)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());

            }
            case "FUTURE": {
                return bookingRepository.findAllByOwnerItemsAndFutureStatus(userItemsIds, LocalDateTime.now(), sort)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());

            }
            case "PAST": {
                return bookingRepository.findAllByOwnerItemsAndPastStatus(userItemsIds, LocalDateTime.now(), sort)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            }
            case "ALL": {
                return bookingRepository.findAllByOwnerItems(userItemsIds, sort)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            }
            default:
                throw new UnsupportedStatusException(state);
        }
    }

    private UserDto checkingUserId(Long userId) {
        if (userId == -1) {
            throw new IncorrectDataException("There is no user with header-Id : " + userId);
        }
        return userService.getById(userId);
    }
}
