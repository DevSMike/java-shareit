package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingLiteDto;
import ru.practicum.shareit.booking.dto.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.shareit.item.dto.mapper.ItemMapper.toItem;

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
                .item(ItemMapper.toItemDto(item))
                .booker(UserMapper.toUserDto(booker))
                .itemId(item.getId())
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
        assertEquals(actual.getStart(), booking.getStart());
        assertEquals(actual.getEnd(), booking.getEnd());
        assertNotEquals(actual.getId(), 0);
        assertEquals(actual.getBooker().getId(), booker.getId());
        assertEquals(actual.bookerId(), booker.getId());
        assertEquals(actual.hashCode(), booking.hashCode());
        assertEquals(actual.getItem(), toItem(bookingDto.getItem()));
    }

    @Test
    void toBookingLiteDto() {
        BookingLiteDto actual = BookingMapper.toBookingLiteDto(bookingDto);

        assertEquals(actual.getStatus(), bookingDto.getStatus());
        assertEquals(actual.getEnd(), bookingDto.getEnd());
        assertEquals(actual.getStart(), bookingDto.getStart());
        assertEquals(actual.getItem(), bookingDto.getItem());
        assertNull(actual.getItemId());
        assertEquals(actual.getBookerId(), bookingDto.getBooker().getId());
        assertEquals(actual.getId(), bookingDto.getId());

    }
}