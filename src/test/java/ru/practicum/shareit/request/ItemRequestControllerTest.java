package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.validator.PageableValidator;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ItemRequestControllerTest {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @MockBean
    ItemRequestService itemRequestService;

    @MockBean
    PageableValidator pageableValidator;

    @SneakyThrows
    @Test
    void addNewRequest() {
        ItemRequestDto itemRequestToCreate = new ItemRequestDto();
        when(itemRequestService.addNewRequest(any(ItemRequestDto.class), anyLong())).thenReturn(itemRequestToCreate);

        String result = mockMvc.perform(post("/requests")
                        .content(objectMapper.writeValueAsString(itemRequestToCreate))
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemRequestToCreate), result);
    }

    @SneakyThrows
    @Test
    void getAllUserItemsWithResponses() {
        mockMvc.perform(get("/requests"))
                .andExpect(status().isOk());

        verify(itemRequestService, times(1)).getAllUserRequestsWithResponses(anyLong());
    }

    @SneakyThrows
    @Test
    void getAllCreatedRequests() {
        doNothing().when(pageableValidator).checkingPageableParams(anyInt(), anyInt());

        mockMvc.perform(get("/requests/all")
                    .param("from", "1")
                    .param("size", "1"))
                .andExpect(status().isOk());

        verify(itemRequestService, times(1)).getAllRequestsToResponse(anyLong(), any(Pageable.class));
    }

    @SneakyThrows
    @Test
    void getRequestById() {
        long requestId = 0L;
        mockMvc.perform(get("/requests/{requestId}", requestId))
                .andExpect(status().isOk());

        verify(itemRequestService, times(1)).getRequestById(anyLong(), anyLong());
    }
}