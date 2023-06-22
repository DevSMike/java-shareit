package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.EmptyFieldException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceDbImpl;
import ru.practicum.shareit.validator.UserValidator;

import java.util.Collection;
import java.util.Collections;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.user.dto.mapper.UserMapper.toUserDto;


@ExtendWith(MockitoExtension.class)
class UserServiceDbImplTest {

    @Mock
    UserRepository userRepository;

    @Mock
    UserValidator userValidator;

    @InjectMocks
    UserServiceDbImpl userService;


    @Test
    void create_whenAllDataIsCorrect_thenReturnCorrectUser() {
        User expectedUser = new User();
        expectedUser.setEmail("test@mail.ru");
        when(userRepository.save(expectedUser)).thenReturn(expectedUser);

        UserDto actualUser = userService.create(toUserDto(expectedUser));

        assertEquals(expectedUser.getEmail(), actualUser.getEmail(), "emails are different");
        verify(userValidator, times(1))
                .validateUserData(toUserDto(expectedUser));
    }

    @Test
    void create_whenDataIsIncorrect_thenThrowEmptyFieldExceptionException() {
        User expectedUser = new User();
        when(userRepository.save(expectedUser)).thenThrow(new EmptyFieldException("User's email is empty"));

        EmptyFieldException emptyFieldException = assertThrows(EmptyFieldException.class,
                () -> userService.create(toUserDto(expectedUser)), "exceptions are diff");
        assertEquals(emptyFieldException.getMessage(), "User's email is empty", "messages are diff");
    }

    @Test
    void getById_whenUserFound_thenReturnUser() {
        long userId = 0L;
        User expectedUser = new User();
        expectedUser.setEmail("test@mail.ru");
        when(userValidator.validateUserIdAndReturn(userId)).thenReturn(expectedUser);

        UserDto actualUser = userService.getById(userId);

        assertEquals(toUserDto(expectedUser), actualUser, "objects are diff");
    }

    @Test
    void getById_whenUserNotFound_thenThrowEntityNotFoundException() {
        long userId = 0L;
        User expectedUser = new User();
        expectedUser.setEmail("test@mail.ru");
        when(userValidator.validateUserIdAndReturn(userId)).thenThrow(new EntityNotFoundException("There is no user : " + userId));

        EntityNotFoundException entityNotFoundException = assertThrows(EntityNotFoundException.class,
                () -> userService.getById(userId));

        assertEquals(entityNotFoundException.getMessage(), "There is no user : 0", "messages are diff");
    }

    @Test
    void getAll_whenDataExists_thenReturnEmptyCollection() {
        when(userService.getAll()).thenReturn(Collections.emptyList());

        Collection<UserDto> users = userService.getAll();

        assertEquals(users.size(), 0, "sizes are diff");
    }

    @Test
    void update_whenUserExists_thenReturnUpdatedUser() {
        long userId = 0L;
        User updateUser = new User();
        updateUser.setEmail("update@mail.ru");
        when(userValidator.validateUserIdAndReturn(userId)).thenReturn(updateUser);

        UserDto actualUser = userService.update(toUserDto(updateUser));

        assertEquals(toUserDto(updateUser), actualUser, "users are diff");
        verify(userRepository, times(1))
                .save(updateUser);
    }

    @Test
    void update_whenUserNotExists_thenThrowEntityNotFoundException() {
        long userId = 0L;
        User expectedUser = new User();
        expectedUser.setEmail("update@mail.ru");
        when(userValidator.validateUserIdAndReturn(userId)).thenThrow(new EntityNotFoundException("There is no user : " + userId));

        EntityNotFoundException entityNotFoundException = assertThrows(EntityNotFoundException.class,
                () -> userService.update(toUserDto(expectedUser)));

        assertEquals(entityNotFoundException.getMessage(), "There is no user : 0", "messages are diff");
    }

    @Test
    void delete_whenUserExists_thenDeleteUser() {
        long userId = 0L;
        User expectedUser = new User();
        expectedUser.setEmail("test@mail.ru");
        when(userValidator.validateUserIdAndReturn(userId)).thenReturn(expectedUser);

        userService.delete(userId);

        verify(userRepository, times(1))
                .deleteById(0L);
    }

    @Test
    void delete_whenUserNotExists_thenThrowEntityNotFoundException() {
        long userId = 0L;
        User expectedUser = new User();
        expectedUser.setEmail("test@mail.ru");
        when(userValidator.validateUserIdAndReturn(userId)).thenThrow(new EntityNotFoundException("There is no user : " + userId));

        EntityNotFoundException entityNotFoundException = assertThrows(EntityNotFoundException.class,
                () -> userService.delete(userId));

        assertEquals(entityNotFoundException.getMessage(), "There is no user : 0", "messages are diff");
    }
}