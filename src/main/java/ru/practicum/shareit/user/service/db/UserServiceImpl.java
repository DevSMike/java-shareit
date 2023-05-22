package ru.practicum.shareit.user.service.db;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EmptyFieldException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepositoryDb;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;
import java.util.stream.Collectors;

import static ru.practicum.shareit.user.dto.mapper.UserMapper.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepositoryDb userRepository;

    @Override
    public UserDto create(UserDto userDto) {
        if (userDto.getEmail() == null) {
            throw new EmptyFieldException("Email is empty");
        }
        log.debug("Creating user : {}", userDto);
        return toUserDto(userRepository.save(toUser(userDto)));
    }

    @Override
    public UserDto getById(long id) {
        log.debug("Getting user by Id: {}", id);
        User userFromRep = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("There is no User with id: " + id));
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
        User userToUpdate = toUserUpdate(userDto, userRepository.findById(userDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("There is no User with id: " + userDto.getId())));
        userRepository.update(userToUpdate.getName(), userToUpdate.getEmail(), userToUpdate.getId());
        return toUserDto(userToUpdate);
    }

    @Override
    public void delete(long id) {
        User userFromDb = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("There is no User with id: " + id));
        log.debug("Deleting user by id: {}", id);
        userRepository.deleteById(userFromDb.getId());
    }

//    private void checkingId(long id) {
//        if (userRepository.findById(id).orElse(null) == null) {
//            throw ;
//        }
//    }
}
