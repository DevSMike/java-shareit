package ru.practicum.shareit.validator;

import ru.practicum.shareit.exception.EmptyFieldException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.IncorrectDataException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;


public class UserValidator {



    public static void validateUserId(long userId, UserRepository repository) {
        if (userId == -1) {
            throw new IncorrectDataException("There is no user with header-Id : " + userId);
        }
        repository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("There is no user : " + userId));
    }

    public static User validateUserIdAndReturn(long userId, UserRepository repository) {
        if (userId == -1) {
            throw new IncorrectDataException("There is no user with header-Id : " + userId);
        }
        return  repository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("There is no user : " + userId));
    }
    public static void validateUserData(UserDto userDto) {
        if (userDto.getEmail() == null || userDto.getEmail().isEmpty()) {
            throw new EmptyFieldException("User's email is empty");
        }
    }
}
