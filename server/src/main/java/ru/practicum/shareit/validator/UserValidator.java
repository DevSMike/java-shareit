package ru.practicum.shareit.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.EmptyFieldException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.IncorrectDataException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;


@Component
@RequiredArgsConstructor
public class UserValidator {

    private final UserRepository repository;

    public  void validateUserId(long userId) {
        if (userId == -1) {
            throw new IncorrectDataException("There is no user with header-Id : " + userId);
        }
        repository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("There is no user : " + userId));
    }

    public  User validateUserIdAndReturn(long userId) {
        if (userId == -1) {
            throw new IncorrectDataException("There is no user with header-Id : " + userId);
        }
        return  repository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("There is no user : " + userId));
    }

    public  void validateUserData(UserDto userDto) {
        if (userDto.getEmail() == null || userDto.getEmail().isEmpty()) {
            throw new EmptyFieldException("User's email is empty");
        }
    }
}
