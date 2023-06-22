package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.IncorrectDataException;
import ru.practicum.shareit.exception.UnsupportedStatusException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.validator.BookingValidator;
import ru.practicum.shareit.validator.ItemValidator;
import ru.practicum.shareit.validator.UserValidator;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.booking.dto.mapper.BookingMapper.toBookingDto;


@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    private static final Pageable PAGE_FOR_BOOKINGS = PageRequest.of(0, 10, Sort.by("start").descending());

    private User owner;
    private Item ownerItem;
    private Booking booking;

    @Mock
    ItemRepository itemRepository;

    @Mock
    BookingRepository bookingRepository;

    @Mock
    UserValidator userValidator;

    @Mock
    ItemValidator itemValidator;

    @Mock
    BookingValidator bookingValidator;

    @InjectMocks
    BookingServiceImpl bookingService;

    @BeforeEach
    public void fillData() {
        owner = User.builder()
                .id(1L)
                .name("name")
                .email("email@mai.ru")
                .bookings(List.of(new Booking()))
                .items(List.of(new Item()))
                .build();

        ownerItem = Item.builder()
                .id(1L)
                .description("desc")
                .name("name")
                .owner(owner)
                .available(true)
                .build();

        booking = Booking.builder()
                .id(1L)
                .status(BookingStatus.WAITING)
                .item(ownerItem)
                .booker(new User())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
    }

    @Test
    void addBooking_whenUserAndItemExistAndAllDataIsCorrect_thenReturnBooking() {
        when(userValidator.validateUserIdAndReturn(anyLong())).thenReturn(owner);
        when(itemValidator.validateItemIdAndReturns(anyLong())).thenReturn(ownerItem);
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingDto expectedBooking = bookingService.addBooking(toBookingDto(booking), 2L);

        assertEquals(expectedBooking, toBookingDto(booking));
    }

    @Test
    void addBooking_whenItemOwnerIdEqualsBookerId_thenThrowEntityNotFoundException() {
        when(userValidator.validateUserIdAndReturn(anyLong())).thenReturn(owner);
        when(itemValidator.validateItemIdAndReturns(anyLong())).thenReturn(ownerItem);

        EntityNotFoundException entityNotFoundException = assertThrows(EntityNotFoundException.class,
                () -> bookingService.addBooking(toBookingDto(booking), 1L));

        assertEquals(entityNotFoundException.getMessage(), "Owner can't book his item");
    }

    @Test
    void addBooking_whenItemIsNotAvailable_thenThrowIncorrectDataException() {
        when(userValidator.validateUserIdAndReturn(anyLong())).thenReturn(owner);
        when(itemValidator.validateItemIdAndReturns(anyLong())).thenReturn(ownerItem);
        ownerItem.setAvailable(false);

        IncorrectDataException incorrectDataException = assertThrows(IncorrectDataException.class,
                () -> bookingService.addBooking(toBookingDto(booking), 2L));

        assertEquals(incorrectDataException.getMessage(), "Booking: Item is unavailable");
    }

    @Test
    void addBooking_whenBookingDatesAreNull_thenThrowIncorrectDataException() {
        when(userValidator.validateUserIdAndReturn(anyLong())).thenReturn(owner);
        when(itemValidator.validateItemIdAndReturns(anyLong())).thenReturn(ownerItem);
        booking.setStart(null);

        IncorrectDataException incorrectDataException = assertThrows(IncorrectDataException.class,
                () -> bookingService.addBooking(toBookingDto(booking), 2L));

        assertEquals(incorrectDataException.getMessage(), "Booking: Dates are null!");
    }

    @Test
    void addBooking_whenBookingDatesAreEquals_thenThrowIncorrectDataException() {
        when(userValidator.validateUserIdAndReturn(anyLong())).thenReturn(owner);
        when(itemValidator.validateItemIdAndReturns(anyLong())).thenReturn(ownerItem);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now());

        IncorrectDataException incorrectDataException = assertThrows(IncorrectDataException.class,
                () -> bookingService.addBooking(toBookingDto(booking), 2L));

        assertEquals(incorrectDataException.getMessage(), "Booking: Problem in dates");
    }

    @Test
    void addBooking_whenUserNotFound_thenThrowEntityNotFoundException() {
        doThrow(new EntityNotFoundException("User not found"))
                .when(userValidator).validateUserIdAndReturn(anyLong());

        EntityNotFoundException entityNotFoundException = assertThrows(EntityNotFoundException.class,
                () -> bookingService.addBooking(toBookingDto(booking), 2L));

        assertEquals(entityNotFoundException.getMessage(), "User not found");
    }

    @Test
    void addBooking_whenItemNotFound_thenThrowEntityNotFoundException() {
        when(userValidator.validateUserIdAndReturn(anyLong())).thenReturn(owner);
        when(itemValidator.validateItemIdAndReturns(anyLong())).thenThrow(new EntityNotFoundException("Item not found"));

        EntityNotFoundException entityNotFoundException = assertThrows(EntityNotFoundException.class,
                () -> bookingService.addBooking(toBookingDto(booking), 2L));

        assertEquals(entityNotFoundException.getMessage(), "Item not found");
    }

    @Test
    void approveBooking_whenUserAndItemExistAndAllDataCorrect_thenReturnBooking() {
        doNothing().when(userValidator).validateUserId(anyLong());
        when(bookingValidator.validateBookingIdAndReturns(anyLong())).thenReturn(booking);

        BookingDto actualBooking = bookingService.approveBooking(1L, 1L, "true");

        assertEquals(actualBooking.getId(), booking.getId());
        assertEquals(actualBooking.getStart(), booking.getStart());
    }

    @Test
    void approveBooking_whenUserAndItemExistAnApproveIncorrect_thenThrowIncorrectDataException() {
        doNothing().when(userValidator).validateUserId(anyLong());
        when(bookingValidator.validateBookingIdAndReturns(anyLong())).thenReturn(booking);

        IncorrectDataException exception = assertThrows(IncorrectDataException.class,
                () -> bookingService.approveBooking(1L, 1L, "incorrect"));

        assertEquals(exception.getMessage(), "Incorrect data in approve method");
    }

    @Test
    void approveBooking_whenUserAndItemExistAndUserIsNotOwner_thenThrowEntityNotFoundException() {
        doNothing().when(userValidator).validateUserId(anyLong());
        when(bookingValidator.validateBookingIdAndReturns(anyLong())).thenReturn(booking);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> bookingService.approveBooking(1L, 2L, "true"));

        assertEquals(exception.getMessage(), "User with id = 2 is not an owner!");
    }

    @Test
    void approveBooking_whenUserAndItemExistAndStatusIsApprove_thenThrowIncorrectDataException() {
        doNothing().when(userValidator).validateUserId(anyLong());
        when(bookingValidator.validateBookingIdAndReturns(anyLong())).thenReturn(booking);
        booking.setStatus(BookingStatus.APPROVED);

        IncorrectDataException exception = assertThrows(IncorrectDataException.class,
                () -> bookingService.approveBooking(1L, 1L, "true"));

        assertEquals(exception.getMessage(), "Status is Approved");
    }

    @Test
    void approveBooking_whenUserNotFound_thenThrowEntityNotFoundException() {
        doThrow(new EntityNotFoundException("User not found"))
                .when(userValidator).validateUserId(anyLong());

        EntityNotFoundException entityNotFoundException = assertThrows(EntityNotFoundException.class,
                () -> bookingService.approveBooking(1L, 1L, "true"));

        assertEquals(entityNotFoundException.getMessage(), "User not found");
    }

    @Test
    void approveBooking_whenBookingNotFound_thenThrowEntityNotFoundException() {
        doThrow(new EntityNotFoundException("Booking not found"))
                .when(bookingValidator).validateBookingIdAndReturns(anyLong());

        EntityNotFoundException entityNotFoundException = assertThrows(EntityNotFoundException.class,
                () -> bookingService.approveBooking(1L, 1L, "true"));

        assertEquals(entityNotFoundException.getMessage(), "Booking not found");
    }

    @Test
    void getBookingInfo_whenUserIsOwnerAndBookingExist_thenReturnBooking() {
        doNothing().when(userValidator).validateUserId(anyLong());
        when(bookingValidator.validateBookingIdAndReturns(anyLong())).thenReturn(booking);

        BookingDto actualBooking = bookingService.getBookingInfo(1L, 1L);

        assertEquals(actualBooking.getStatus(), booking.getStatus());
    }

    @Test
    void getBookingInfo_whenUserIsNotOwner_thenThrowEntityNotFoundException() {
        doNothing().when(userValidator).validateUserId(anyLong());
        when(bookingValidator.validateBookingIdAndReturns(anyLong())).thenReturn(booking);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> bookingService.getBookingInfo(1L, 2L));

        assertEquals(exception.getMessage(), "User with id = 2 is not an owner!");
    }

    @Test
    void getBookingInfo_whenBookingNotFound_thenThrowEntityNotFoundException() {
        doNothing().when(userValidator).validateUserId(anyLong());
        when(bookingValidator.validateBookingIdAndReturns(anyLong()))
                .thenThrow(new EntityNotFoundException("Booking not found"));

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> bookingService.getBookingInfo(4L, 1L));

        assertEquals(exception.getMessage(), "Booking not found");
    }

    @Test
    void getAllBookingsByUserId_whenUserAndStateExist_thenReturnListOfBooking() {
        doNothing().when(userValidator).validateUserId(anyLong());
        doNothing().when(bookingValidator).validateBookingState(anyString());
        when(bookingRepository.findAllByBookerIdAndCurrentStatus(anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingDto> actualBookings = bookingService.getAllBookingsByUserId(1L, "CURRENT", PAGE_FOR_BOOKINGS);

        assertEquals(actualBookings.size(), 1);
        verify(bookingRepository, times(0)).findAllByBookerIdAndWaitingStatus(any(), any(), any());
        verify(bookingRepository, times(0)).findAllByBookerIdAndFutureStatus(any(), any(), any());
        verify(bookingRepository, times(0)).findAllByBookerIdAndRejectedStatus(any(), any(), any());
        verify(bookingRepository, times(0)).findAllByBookerIdAndPastStatus(any(), any(), any());
        verify(bookingRepository, times(0)).findAllByBooker_Id(any(), any());
    }

    @Test
    void getAllBookingsByUserId_whenUserAndStateExistAndWaiting_thenReturnListOfBooking() {
        doNothing().when(userValidator).validateUserId(anyLong());
        doNothing().when(bookingValidator).validateBookingState(anyString());
        when(bookingRepository.findAllByBookerIdAndWaitingStatus(anyLong(), any(), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingDto> actualBookings = bookingService.getAllBookingsByUserId(1L, "WAITING", PAGE_FOR_BOOKINGS);

        assertEquals(actualBookings.size(), 1);

        verify(bookingRepository, times(0)).findAllByBookerIdAndCurrentStatus(any(), any(), any());
        verify(bookingRepository, times(0)).findAllByBookerIdAndFutureStatus(any(), any(), any());
        verify(bookingRepository, times(0)).findAllByBookerIdAndRejectedStatus(any(), any(), any());
        verify(bookingRepository, times(0)).findAllByBookerIdAndPastStatus(any(), any(), any());
        verify(bookingRepository, times(0)).findAllByBooker_Id(any(), any());
    }

    @Test
    void getAllBookingsByUserId_whenUserAndStateExistAndFuture_thenReturnListOfBooking() {
        doNothing().when(userValidator).validateUserId(anyLong());
        doNothing().when(bookingValidator).validateBookingState(anyString());
        when(bookingRepository.findAllByBookerIdAndFutureStatus(anyLong(), any(), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingDto> actualBookings = bookingService.getAllBookingsByUserId(1L, "FUTURE", PAGE_FOR_BOOKINGS);

        assertEquals(actualBookings.size(), 1);

        verify(bookingRepository, times(0)).findAllByBookerIdAndCurrentStatus(any(), any(), any());
        verify(bookingRepository, times(0)).findAllByBookerIdAndWaitingStatus(any(), any(), any());
        verify(bookingRepository, times(0)).findAllByBookerIdAndRejectedStatus(any(), any(), any());
        verify(bookingRepository, times(0)).findAllByBookerIdAndPastStatus(any(), any(), any());
        verify(bookingRepository, times(0)).findAllByBooker_Id(any(), any());
    }

    @Test
    void getAllBookingsByUserId_whenUserAndStateExistAndRejected_thenReturnListOfBooking() {
        doNothing().when(userValidator).validateUserId(anyLong());
        doNothing().when(bookingValidator).validateBookingState(anyString());
        when(bookingRepository.findAllByBookerIdAndRejectedStatus(anyLong(), any(), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingDto> actualBookings = bookingService.getAllBookingsByUserId(1L, "REJECTED", PAGE_FOR_BOOKINGS);

        assertEquals(actualBookings.size(), 1);

        verify(bookingRepository, times(0)).findAllByBookerIdAndCurrentStatus(any(), any(), any());
        verify(bookingRepository, times(0)).findAllByBookerIdAndWaitingStatus(any(), any(), any());
        verify(bookingRepository, times(0)).findAllByBookerIdAndFutureStatus(any(), any(), any());
        verify(bookingRepository, times(0)).findAllByBookerIdAndPastStatus(any(), any(), any());
        verify(bookingRepository, times(0)).findAllByBooker_Id(any(), any());
    }

    @Test
    void getAllBookingsByUserId_whenUserAndStateExistAndPast_thenReturnListOfBooking() {
        doNothing().when(userValidator).validateUserId(anyLong());
        doNothing().when(bookingValidator).validateBookingState(anyString());
        when(bookingRepository.findAllByBookerIdAndPastStatus(anyLong(), any(), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingDto> actualBookings = bookingService.getAllBookingsByUserId(1L, "PAST", PAGE_FOR_BOOKINGS);

        assertEquals(actualBookings.size(), 1);

        verify(bookingRepository, times(0)).findAllByBookerIdAndCurrentStatus(any(), any(), any());
        verify(bookingRepository, times(0)).findAllByBookerIdAndWaitingStatus(any(), any(), any());
        verify(bookingRepository, times(0)).findAllByBookerIdAndFutureStatus(any(), any(), any());
        verify(bookingRepository, times(0)).findAllByBookerIdAndRejectedStatus(any(), any(), any());
        verify(bookingRepository, times(0)).findAllByBooker_Id(any(), any());
    }

    @Test
    void getAllBookingsByUserId_whenUserAndStateExistAndAll_thenReturnListOfBooking() {
        doNothing().when(userValidator).validateUserId(anyLong());
        doNothing().when(bookingValidator).validateBookingState(anyString());
        when(bookingRepository.findAllByBooker_Id(anyLong(), any()))
                .thenReturn(List.of(booking));

        List<BookingDto> actualBookings = bookingService.getAllBookingsByUserId(1L, "ALL", PAGE_FOR_BOOKINGS);

        assertEquals(actualBookings.size(), 1);

        verify(bookingRepository, times(0)).findAllByBookerIdAndCurrentStatus(any(), any(), any());
        verify(bookingRepository, times(0)).findAllByBookerIdAndWaitingStatus(any(), any(), any());
        verify(bookingRepository, times(0)).findAllByBookerIdAndFutureStatus(any(), any(), any());
        verify(bookingRepository, times(0)).findAllByBookerIdAndRejectedStatus(any(), any(), any());
        verify(bookingRepository, times(0)).findAllByBookerIdAndPastStatus(any(), any(), any());
    }

    @Test
    void getAllBookingsByUserId_whenUserNotExists_thenThrowEntityNotFoundException() {
        doThrow(new EntityNotFoundException("User not found")).when(userValidator).validateUserId(anyLong());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> bookingService.getAllBookingsByUserId(1L, "CURRENT", PAGE_FOR_BOOKINGS));

        assertEquals(exception.getMessage(), "User not found");
    }

    @Test
    void getAllBookingsByUserId_whenUserAndStateIncorrect_thenThrowUnsupportedStatusException() {
        doNothing().when(userValidator).validateUserId(anyLong());
        doThrow(new UnsupportedStatusException("state"))
                .when(bookingValidator).validateBookingState(anyString());

        UnsupportedStatusException exception = assertThrows(UnsupportedStatusException.class,
                () -> bookingService.getAllBookingsByUserId(1L, "NOT", PAGE_FOR_BOOKINGS));

        assertEquals(exception.getMessage(), "state");
    }

    @Test
    void getAllBookingsByOwnerId_whenUserAndBookingAndItemExist_thenReturnListOfBooking() {
        doNothing().when(userValidator).validateUserId(anyLong());
        doNothing().when(bookingValidator).validateBookingState(anyString());
        when(itemRepository.findByOwner_Id_WithoutPageable(anyLong())).thenReturn(List.of(ownerItem));
        when(bookingRepository.findAllByOwnerItemsAndCurrentStatus(anyList(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingDto> actualBookings = bookingService.getAllBookingsByOwnerId(1L, "CURRENT", PAGE_FOR_BOOKINGS);

        assertEquals(actualBookings.size(), 1);
        verify(bookingRepository, times(0)).findAllByOwnerItemsAndWaitingStatus(any(), any(), any());
        verify(bookingRepository, times(0)).findAllByOwnerItemsAndFutureStatus(any(), any(), any());
        verify(bookingRepository, times(0)).findAllByOwnerItemsAndRejectedStatus(any(), any(), any());
        verify(bookingRepository, times(0)).findAllByOwnerItemsAndPastStatus(any(), any(), any());
        verify(bookingRepository, times(0)).findAllByOwnerItems(any(), any());
    }

    @Test
    void getAllBookingsByOwnerId_whenUserAndBookingAndItemExistWaiting_thenReturnListOfBooking() {
        doNothing().when(userValidator).validateUserId(anyLong());
        doNothing().when(bookingValidator).validateBookingState(anyString());
        when(itemRepository.findByOwner_Id_WithoutPageable(anyLong())).thenReturn(List.of(ownerItem));
        when(bookingRepository.findAllByOwnerItemsAndWaitingStatus(anyList(), any(), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingDto> actualBookings = bookingService.getAllBookingsByOwnerId(1L, "WAITING", PAGE_FOR_BOOKINGS);

        assertEquals(actualBookings.size(), 1);
        verify(bookingRepository, times(0)).findAllByOwnerItemsAndCurrentStatus(any(), any(), any());
        verify(bookingRepository, times(0)).findAllByOwnerItemsAndFutureStatus(any(), any(), any());
        verify(bookingRepository, times(0)).findAllByOwnerItemsAndRejectedStatus(any(), any(), any());
        verify(bookingRepository, times(0)).findAllByOwnerItemsAndPastStatus(any(), any(), any());
        verify(bookingRepository, times(0)).findAllByOwnerItems(any(), any());
    }

    @Test
    void getAllBookingsByOwnerId_whenUserAndBookingAndItemExistFuture_thenReturnListOfBooking() {
        doNothing().when(userValidator).validateUserId(anyLong());
        doNothing().when(bookingValidator).validateBookingState(anyString());
        when(itemRepository.findByOwner_Id_WithoutPageable(anyLong())).thenReturn(List.of(ownerItem));
        when(bookingRepository.findAllByOwnerItemsAndFutureStatus(anyList(), any(), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingDto> actualBookings = bookingService.getAllBookingsByOwnerId(1L, "FUTURE", PAGE_FOR_BOOKINGS);

        assertEquals(actualBookings.size(), 1);
        verify(bookingRepository, times(0)).findAllByOwnerItemsAndCurrentStatus(any(), any(), any());
        verify(bookingRepository, times(0)).findAllByOwnerItemsAndWaitingStatus(any(), any(), any());
        verify(bookingRepository, times(0)).findAllByOwnerItemsAndRejectedStatus(any(), any(), any());
        verify(bookingRepository, times(0)).findAllByOwnerItemsAndPastStatus(any(), any(), any());
        verify(bookingRepository, times(0)).findAllByOwnerItems(any(), any());
    }

    @Test
    void getAllBookingsByOwnerId_whenUserAndBookingAndItemExistRejected_thenReturnListOfBooking() {
        doNothing().when(userValidator).validateUserId(anyLong());
        doNothing().when(bookingValidator).validateBookingState(anyString());
        when(itemRepository.findByOwner_Id_WithoutPageable(anyLong())).thenReturn(List.of(ownerItem));
        when(bookingRepository.findAllByOwnerItemsAndRejectedStatus(anyList(), any(), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingDto> actualBookings = bookingService.getAllBookingsByOwnerId(1L, "REJECTED", PAGE_FOR_BOOKINGS);

        assertEquals(actualBookings.size(), 1);
        verify(bookingRepository, times(0)).findAllByOwnerItemsAndCurrentStatus(any(), any(), any());
        verify(bookingRepository, times(0)).findAllByOwnerItemsAndWaitingStatus(any(), any(), any());
        verify(bookingRepository, times(0)).findAllByOwnerItemsAndFutureStatus(any(), any(), any());
        verify(bookingRepository, times(0)).findAllByOwnerItemsAndPastStatus(any(), any(), any());
        verify(bookingRepository, times(0)).findAllByOwnerItems(any(), any());
    }

    @Test
    void getAllBookingsByOwnerId_whenUserAndBookingAndItemExistPast_thenReturnListOfBooking() {
        doNothing().when(userValidator).validateUserId(anyLong());
        doNothing().when(bookingValidator).validateBookingState(anyString());
        when(itemRepository.findByOwner_Id_WithoutPageable(anyLong())).thenReturn(List.of(ownerItem));
        when(bookingRepository.findAllByOwnerItemsAndPastStatus(anyList(), any(), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingDto> actualBookings = bookingService.getAllBookingsByOwnerId(1L, "PAST", PAGE_FOR_BOOKINGS);

        assertEquals(actualBookings.size(), 1);
        verify(bookingRepository, times(0)).findAllByOwnerItemsAndCurrentStatus(any(), any(), any());
        verify(bookingRepository, times(0)).findAllByOwnerItemsAndWaitingStatus(any(), any(), any());
        verify(bookingRepository, times(0)).findAllByOwnerItemsAndFutureStatus(any(), any(), any());
        verify(bookingRepository, times(0)).findAllByOwnerItemsAndRejectedStatus(any(), any(), any());
        verify(bookingRepository, times(0)).findAllByOwnerItems(any(), any());
    }

    @Test
    void getAllBookingsByOwnerId_whenUserAndBookingAndItemExistAll_thenReturnListOfBooking() {
        doNothing().when(userValidator).validateUserId(anyLong());
        doNothing().when(bookingValidator).validateBookingState(anyString());
        when(itemRepository.findByOwner_Id_WithoutPageable(anyLong())).thenReturn(List.of(ownerItem));
        when(bookingRepository.findAllByOwnerItems(any(), any()))
                .thenReturn(List.of(booking));

        List<BookingDto> actualBookings = bookingService.getAllBookingsByOwnerId(1L, "ALL", PAGE_FOR_BOOKINGS);

        assertEquals(actualBookings.size(), 1);
        verify(bookingRepository, times(0)).findAllByOwnerItemsAndCurrentStatus(any(), any(), any());
        verify(bookingRepository, times(0)).findAllByOwnerItemsAndWaitingStatus(any(), any(), any());
        verify(bookingRepository, times(0)).findAllByOwnerItemsAndFutureStatus(any(), any(), any());
        verify(bookingRepository, times(0)).findAllByOwnerItemsAndRejectedStatus(any(), any(), any());
        verify(bookingRepository, times(0)).findAllByOwnerItemsAndPastStatus(any(), any(), any());
    }

    @Test
    void getAllBookingsByOwnerId_whenUserNotExists_thenThrowEntityNotFoundException() {
        doThrow(new EntityNotFoundException("User not found")).when(userValidator).validateUserId(anyLong());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> bookingService.getAllBookingsByOwnerId(1L, "CURRENT", PAGE_FOR_BOOKINGS));

        assertEquals(exception.getMessage(), "User not found");
    }

    @Test
    void getAllBookingsByOwnerId_whenUserAndStateIncorrect_thenThrowUnsupportedStatusException() {
        doNothing().when(userValidator).validateUserId(anyLong());
        doThrow(new UnsupportedStatusException("state"))
                .when(bookingValidator).validateBookingState(anyString());

        UnsupportedStatusException exception = assertThrows(UnsupportedStatusException.class,
                () -> bookingService.getAllBookingsByOwnerId(1L, "NOT", PAGE_FOR_BOOKINGS));

        assertEquals(exception.getMessage(), "state");
    }
}