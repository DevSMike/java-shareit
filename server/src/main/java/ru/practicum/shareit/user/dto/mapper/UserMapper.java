package ru.practicum.shareit.user.dto.mapper;

import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;

public class UserMapper {

    public static UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static User toUser(UserDto userDto) {
        return User.builder()
                .id(userDto.getId() != null ? userDto.getId() : 0)
                .name(userDto.getName())
                .email(userDto.getEmail())
                .build();
    }

    public static User toUserUpdate(UserDto userDto, User user) {
        return User.builder()
                .id(userDto.getId())
                .name(userDto.getName() != null ? userDto.getName() : user.getName())
                .email(userDto.getEmail() != null ? userDto.getEmail() : user.getEmail())
                .build();
    }
}
