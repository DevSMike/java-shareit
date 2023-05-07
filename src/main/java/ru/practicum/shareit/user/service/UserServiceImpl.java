package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EmptyFieldException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.shareit.user.dto.mapper.UserMapper.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto create(UserDto userDto) {
        if (userDto.getEmail() == null) {
            throw new EmptyFieldException("Email is empty");
        }
        log.debug("Creating user : {}", userDto);
        return toUserDto(userRepository.create(toUser(userDto)));
    }

    @Override
    public UserDto getById(long id) {
        checkingId(id);
        log.debug("Getting user by Id: {}", id);
        return toUserDto(userRepository.getById(id));
    }

    @Override
    public Collection<UserDto> getAll() {
        log.debug("Getting all users");
        return userRepository.getAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto update(UserDto userDto) {
        checkingId(userDto.getId());
        log.debug("Updating user: {}", userDto);
        return toUserDto(userRepository
                .update(toUserUpdate(userDto, userRepository.getById(userDto.getId()))));
    }

    @Override
    public void delete(long id) {
        checkingId(id);
        log.debug("Deleting user by id: {}", id);
        userRepository.delete(id);
    }

    private void checkingId(long id) {
        if (userRepository.getAll().stream()
                .map(UserMapper::toUserDto)
                .map(UserDto::getId)
                .noneMatch(x -> x.equals(id))) {
            throw new EntityNotFoundException("There is no User with id: " + id);
        }
    }
}
