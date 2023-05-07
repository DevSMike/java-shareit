package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;


import java.util.Collection;

public interface UserService {

    UserDto create(UserDto user);

    UserDto getById(long id);

    Collection<UserDto> getAll();

    UserDto update(UserDto user);

    void delete(long id);
}
