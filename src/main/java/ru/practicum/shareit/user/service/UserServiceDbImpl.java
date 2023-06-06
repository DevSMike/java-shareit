package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.validator.UserValidator;

import java.util.Collection;
import java.util.stream.Collectors;

import static ru.practicum.shareit.user.dto.mapper.UserMapper.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceDbImpl implements UserService {

    private final UserRepository userRepository;
    private final UserValidator userValidator;

    @Override
    public UserDto create(UserDto userDto) {
        log.debug("Creating user : {}", userDto);
        userValidator.validateUserData(userDto);
        return toUserDto(userRepository.save(toUser(userDto)));
    }

    @Override
    public UserDto getById(long id) {
        log.debug("Getting user by Id: {}", id);
        User userFromRep = userValidator.validateUserIdAndReturn(id);
        return toUserDto(userFromRep);
    }

    @Override
    public Collection<UserDto> getAll() {
        log.debug("Getting all users");
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto update(UserDto userDto) {
        log.debug("Updating user: {}", userDto);
        User userToUpdate = toUserUpdate(userDto, userValidator.validateUserIdAndReturn(userDto.getId()));
        userRepository.save(userToUpdate);
        return toUserDto(userToUpdate);
    }

    @Override
    public void delete(long id) {
        User userFromDb = userValidator.validateUserIdAndReturn(id);
        log.debug("Deleting user by id: {}", id);
        userRepository.deleteById(userFromDb.getId());
    }
}
