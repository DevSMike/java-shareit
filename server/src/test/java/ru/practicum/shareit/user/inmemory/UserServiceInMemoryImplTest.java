package ru.practicum.shareit.user.inmemory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.inmemory.UserRepositoryInMemoryImpl;
import ru.practicum.shareit.user.service.UserServiceInMemoryImpl;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.user.dto.mapper.UserMapper.toUserDto;

@ExtendWith(MockitoExtension.class)
class UserServiceInMemoryImplTest {

    private User user;

    @Mock
    UserRepositoryInMemoryImpl userRepositoryInMemory;

    @InjectMocks
    UserServiceInMemoryImpl userServiceInMemory;

    @BeforeEach
    public void fillData() {
        user = User.builder()
                .id(1L)
                .email("mail@mail.ru")
                .name("name")
                .build();
    }

    @Test
    void create_whenEmailNotNull_thenReturnUser() {
        when(userRepositoryInMemory.create(user)).thenReturn(user);

        UserDto userToCreate = userServiceInMemory.create(toUserDto(user));

        assertEquals(userToCreate.getEmail(), user.getEmail());
    }

    @Test
    void getById_whenUserExists_thenReturnUser() {
        when(userRepositoryInMemory.getById(anyLong())).thenReturn(user);

        UserDto userGetById = userServiceInMemory.getById(1L);
        assertEquals(userGetById.getEmail(), user.getEmail());
    }

    @Test
    void getAll_whenItemExists_thenReturnListOfUser() {
        when(userRepositoryInMemory.getAll()).thenReturn(List.of(user));

        List<UserDto> users = new ArrayList<>(userServiceInMemory.getAll());

        assertEquals(users.size(), 1);
    }

    @Test
    void update_whenUserExists_thenReturnUpdatedUser() {
        User updateUser = user;
        updateUser.setName("updateName");
        when(userRepositoryInMemory.getById(anyLong())).thenReturn(user);
        when(userRepositoryInMemory.update(user)).thenReturn(updateUser);

        UserDto updatedFromMap = userServiceInMemory.update(toUserDto(user));

        assertEquals(updatedFromMap.getName(), "updateName");
    }

    @Test
    void delete() {
        when(userRepositoryInMemory.getById(anyLong())).thenReturn(user);

        userServiceInMemory.delete(1L);

        verify(userRepositoryInMemory, times(1)).delete(1L);
    }

}