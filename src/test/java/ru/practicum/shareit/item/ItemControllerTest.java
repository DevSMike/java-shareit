package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.comment.CommentDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.validator.PageableValidator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ItemControllerTest {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @MockBean
    ItemService itemService;

    @MockBean
    PageableValidator pageableValidator;

    @SneakyThrows
    @Test
    void createItem() {
        ItemDto itemToCreate = new ItemDto();
        when(itemService.create(any(ItemDto.class), anyLong())).thenReturn(itemToCreate);

        String result = mockMvc.perform(post("/items")
                    .contentType("application/json")
                    .content(objectMapper.writeValueAsString(itemToCreate)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemToCreate), result);
    }


    @SneakyThrows
    @Test
    void updateItem() {
        ItemDto itemToCreate = ItemDto.builder()
                .id(1L)
                .build();
        ItemDto itemToUpdate = ItemDto.builder()
                .id(2L)
                .build();
        when(itemService.update(any(ItemDto.class), anyLong())).thenReturn(itemToUpdate);

        String result = mockMvc.perform(patch("/items/{itemId}", itemToCreate.getId())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemToCreate)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemToUpdate), result);
    }

    @SneakyThrows
    @Test
    void getItemById() {
        long itemId = 0L;
        mockMvc.perform(get("/items/{itemId}", itemId))
                .andExpect(status().isOk());

        verify(itemService, times(1)).getItemById(anyLong(), anyLong());
    }

    @SneakyThrows
    @Test
    void getUserItems() {
        mockMvc.perform(get("/items")
                        .param("from", "1")
                        .param("size", "1")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk());
        doNothing().when(pageableValidator).checkingPageableParams(anyInt(), anyInt());

        verify(itemService, times(1)).getItemsByUserId(anyLong(), any(Pageable.class));

    }

    @SneakyThrows
    @Test
    void getItemsBySearch_whenCorrectPage_thenReturnOk() {
        mockMvc.perform(get("/items/search")
                        .param("text", "text")
                        .param("from", "1")
                        .param("size", "1"))
                .andExpect(status().isOk());
        doNothing().when(pageableValidator).checkingPageableParams(anyInt(), anyInt());

        verify(itemService, times(1)).getItemsBySearch(anyString(), any(Pageable.class));
    }


    @SneakyThrows
    @Test
    void createCommentToItem() {
        long itemId = 1L;
        CommentDto commentToCreate = new CommentDto();
        when(itemService.addCommentToItem(anyLong(), anyLong(), any(CommentDto.class))).thenReturn(commentToCreate);

        String result = mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .contentType("application/json")
                        .header("X-Sharer-User-Id", "1")
                        .content(objectMapper.writeValueAsString(commentToCreate)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(commentToCreate), result);

    }
}