package ru.practicum.shareit.user.inmemory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.EmailIsAlreadyRegisteredException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.inmemory.UserRepositoryInMemoryImpl;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserRepositoryInMemoryImplTest {

    private User user;

    private UserRepositoryInMemoryImpl userRepositoryInMemory;

    @BeforeEach
    public void fillData() {
        userRepositoryInMemory = new UserRepositoryInMemoryImpl();
        user = User.builder()
                .id(1L)
                .email("mail@mail.ru")
                .name("name")
                .build();
    }

    @Test
    void create_whenEmailIsUnique_thenReturnUser() {
        User userFromMap = userRepositoryInMemory.create(user);

        assertEquals(userFromMap.getId(), 1);
        assertEquals(userFromMap.getName(), user.getName());
    }

    @Test
    void create_whenEmailUnique_thenThrowEmailIsAlreadyRegisteredException() {
        userRepositoryInMemory.create(user);

        EmailIsAlreadyRegisteredException exception = assertThrows(EmailIsAlreadyRegisteredException.class,
                () -> userRepositoryInMemory.create(user));

        assertEquals(exception.getMessage(), "User with this email is already exists!");
    }

    @Test
    void getById_whenUserExists_thenReturnUser() {
        User userFromMap = userRepositoryInMemory.create(user);

        User userGetById = userRepositoryInMemory.getById(userFromMap.getId());

        assertEquals(userFromMap.getName(), userGetById.getName());
    }

    @Test
    void getAll() {
        userRepositoryInMemory.create(user);
        user.setEmail("newEmail@mail.ru");
        userRepositoryInMemory.create(user);

        List<User> users = new ArrayList<>(userRepositoryInMemory.getAll());

        assertEquals(users.size(), 2);
    }

    @Test
    void update_whenUserExists_thenReturnUser() {
        User oldUser = userRepositoryInMemory.create(user);
        oldUser.setName("updateName");

        User userUpdate = userRepositoryInMemory.update(oldUser);

        assertEquals(userUpdate.getName(), "updateName");
    }

    @Test
    void delete() {
        User userFromMap = userRepositoryInMemory.create(user);
        userRepositoryInMemory.delete(userFromMap.getId());

        List<User> users = new ArrayList<>(userRepositoryInMemory.getAll());

        assertEquals(users.size(), 0);
    }
}