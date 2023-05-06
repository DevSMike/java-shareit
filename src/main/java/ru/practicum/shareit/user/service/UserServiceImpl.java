package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EmailIsAlreadyRegisteredException;
import ru.practicum.shareit.exception.EmptyFieldException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.Objects;
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
        containsEmail(userDto);
        log.debug("Creating user : {}", userDto);
        return toUserDto(userRepository.create(toUser(userDto)));
    }

    @Override
    public UserDto getById(int id) {
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
        containsEmail(userDto);
        log.debug("Updating user: {}", userDto);
        return toUserDto(userRepository
                .update(toUserUpdate(userDto, userRepository.getById(userDto.getId()))));
    }

    @Override
    public void delete(int id) {
        checkingId(id);
        log.debug("Deleting user by id: {}", id);
        userRepository.delete(id);
    }

    private void containsEmail(UserDto userDto) {
        if (userRepository.getAll().stream()
                .filter(x -> !Objects.equals(x.getId(), userDto.getId()))
                .anyMatch(x -> x.getEmail().equals(userDto.getEmail()))) {
            throw new EmailIsAlreadyRegisteredException("User with this email is already exists!");
        }
    }

    private void checkingId(int id) {
        if (userRepository.getAll().stream()
                .map(UserMapper::toUserDto)
                .map(UserDto::getId)
                .noneMatch(x -> x.equals(id))) {
            throw new EntityNotFoundException("There is no User with id: " + id);
        }
    }
}
