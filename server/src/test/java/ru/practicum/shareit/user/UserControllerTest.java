package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserService userService;

    @SneakyThrows
    @Test
    void getAllUsers() {
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk());

        verify(userService, times(1)).getAll();
    }

    @SneakyThrows
    @Test
    void getUserById() {
        long userId = 0L;
        mockMvc.perform(get("/users/{userId}", userId))
                .andExpect(status().isOk());

        verify(userService, times(1)).getById(userId);
    }

    @SneakyThrows
    @Test
    void createUser() {
        UserDto userToCreate = new UserDto();
        when(userService.create(userToCreate)).thenReturn(userToCreate);

        String result = mockMvc.perform(post("/users")
                    .contentType("application/json")
                    .content(objectMapper.writeValueAsString(userToCreate)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(userToCreate), result);
    }

    @SneakyThrows
    @Test
    void updateUser() {
        long userId = 0L;
        UserDto userToCreate = new UserDto();
        userToCreate.setId(userId);
        UserDto userUpdated = UserDto.builder()
                .email("updated@mail.ru")
                .id(userId)
                .build();
        when(userService.update(userToCreate)).thenReturn(userUpdated);

        String result = mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userToCreate)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(userUpdated), result);
    }

    @SneakyThrows
    @Test
    void deleteUser() {
        long userId = 0L;
        mockMvc.perform(delete("/users/{userId}", userId))
                .andExpect(status().isOk());

        verify(userService, times(1)).delete(userId);
    }
}