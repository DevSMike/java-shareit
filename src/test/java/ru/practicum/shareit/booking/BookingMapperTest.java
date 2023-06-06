package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingLiteDto;
import ru.practicum.shareit.booking.dto.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BookingMapperTest {

    private Booking booking;
    private BookingDto bookingDto;
    private Item item;
    private User booker;


    @BeforeEach
    public void fillData() {
        booker = User.builder()
                .name("name")
                .id(1L)
                .build();
        item = Item.builder()
                .available(true)
                .id(1L)
                .name("name")
                .description("desc")
                .build();
        booking = Booking.builder()
                .id(1L)
                .status(BookingStatus.WAITING)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1))
                .item(item)
                .booker(booker)
                .build();
        bookingDto = BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .build();
    }

    @Test
    void toBookingDto() {
        BookingDto actual = BookingMapper.toBookingDto(booking);

        assertEquals(actual.getStatus(), booking.getStatus());
        assertEquals(actual.getBooker(), UserMapper.toUserDto(booking.getBooker()));
        assertEquals(actual.getItem().getId(), booking.getItem().getId());
    }

    @Test
    void toBookingDb() {
        Booking actual = BookingMapper.toBookingDb(bookingDto, item, booker);

        assertEquals(actual.getStatus(), bookingDto.getStatus());
        assertEquals(actual.getItem().getId(), item.getId());
        assertEquals(actual.getBooker().getId(), booker.getId());
    }

    @Test
    void toBookingUpdate() {
        bookingDto.setStatus(BookingStatus.APPROVED);
        Booking actual = BookingMapper.toBookingUpdate(bookingDto, booking);

        assertEquals(actual.getStatus(), bookingDto.getStatus());
    }

    @Test
    void toBookingLiteDto() {
        BookingLiteDto actual = BookingMapper.toBookingLiteDto(bookingDto);

        assertEquals(actual.getStatus(), bookingDto.getStatus());
    }
}