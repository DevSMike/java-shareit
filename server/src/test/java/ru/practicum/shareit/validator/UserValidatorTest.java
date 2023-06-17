package ru.practicum.shareit.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.EmptyFieldException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.IncorrectDataException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserValidatorTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserValidator userValidator;

    @Test
    void validateUserId_whenUserNotExists_thenThrowEntityNotFoundException() {
        when(userRepository.findById(anyLong())).thenThrow(new EntityNotFoundException("User not found"));

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> userValidator.validateUserId(1L));

        assertEquals(exception.getMessage(), "User not found");
    }


    @Test
    void validateUserId_whenUserIdLessThanZero_thenThrowIncorrectDataException() {
        IncorrectDataException exception = assertThrows(IncorrectDataException.class,
                () -> userValidator.validateUserId(-1L));

        assertEquals(exception.getMessage(), "There is no user with header-Id : -1");
    }

    @Test
    void validateUserIdAndReturns_whenUserExists_thenReturnUser() {
        User user = new User();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        User actual =  userValidator.validateUserIdAndReturn(1L);

        assertEquals(actual, user);
    }

    @Test
    void validateUserIdAndReturns_whenUserNotExists_thenThrowEntityNotFoundException() {
        when(userRepository.findById(anyLong())).thenThrow(new EntityNotFoundException("User not found"));

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> userValidator.validateUserIdAndReturn(1L));

        assertEquals(exception.getMessage(), "User not found");
    }

    @Test
    void validateUserIdAndReturns_whenUserIdLessThanZero_thenThrowIncorrectDataException() {
        IncorrectDataException exception = assertThrows(IncorrectDataException.class,
                () -> userValidator.validateUserIdAndReturn(-1L));

        assertEquals(exception.getMessage(), "There is no user with header-Id : -1");
    }

    @Test
    void validateUserData_whenDataIncorrect_throwEmptyFieldException() {
        EmptyFieldException exception = assertThrows(EmptyFieldException.class,
                () -> userValidator.validateUserData(new UserDto()));

        assertEquals(exception.getMessage(), "User's email is empty");
    }
}