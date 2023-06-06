package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;


class UserMapperTest {

    @Test
    void toUserDto() {
        User user = User.builder()
                .id(1L)
                .name("name")
                .email("mail@Mail.ru")
                .bookings(new ArrayList<>())
                .items(List.of(Item.builder().id(1L).build()))
                .build();
        User userFromConstructor = new User(1L, "name", "mail@mail.ru", List.of(new Item()), List.of(new Booking()));

        UserDto userDto = UserMapper.toUserDto(user);
        UserDto userDtoConstructor = UserMapper.toUserDto(userFromConstructor);

        assertEquals(user.getId(), userDto.getId());
        assertEquals(user.getName(), userDto.getName());
        assertEquals(user.getItems().size(), 1);
        assertEquals(userDtoConstructor.getName(), userFromConstructor.getName());
        assertEquals(userFromConstructor.getBookings().size(), 1);
    }

    @Test
    void toUser() {
        UserDto userDto = UserDto.builder()
                .id(2L)
                .email("email@mail.ru")
                .name("name")
                .build();
        User user = UserMapper.toUser(userDto);

        assertEquals(userDto.getId(), user.getId());
        assertEquals(userDto.getName(), user.getName());
        assertEquals(userDto.getEmail(), user.getEmail());
        assertNull(user.getItems());
    }

    @Test
    void toUserUpdate() {
        User user = User.builder()
                .id(1L)
                .name("name")
                .email("mail@Mail.ru")
                .build();
        UserDto userDto = UserDto.builder()
                .id(2L)
                .email("email@mail.ru")
                .name("name")
                .build();

        User userToUpdate = UserMapper.toUserUpdate(userDto, user);


        assertEquals(userToUpdate.getId(), userDto.getId());
        assertEquals(userToUpdate.getName(), user.getName());
        assertEquals(userToUpdate.getEmail(), userDto.getEmail());
    }
}