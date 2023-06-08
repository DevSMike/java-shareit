package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.validator.PageableValidator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class BookingControllerTest {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @MockBean
    BookingService bookingService;

    @MockBean
    PageableValidator pageableValidator;

    @SneakyThrows
    @Test
    void createBooking() {
        BookingDto bookingToCreate = new BookingDto();
        when(bookingService.addBooking(any(BookingDto.class), anyLong())).thenReturn(bookingToCreate);

        String result = mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingToCreate))
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(bookingToCreate), result);
    }

    @SneakyThrows
    @Test
    void approveBooking() {
        BookingDto bookingToCreate = new BookingDto();
        bookingToCreate.setId(1L);
        BookingDto updatedBooking = BookingDto.builder()
                .id(1L)
                .status(BookingStatus.APPROVED)
                .build();
        when(bookingService.approveBooking(anyLong(), anyLong(), anyString())).thenReturn(updatedBooking);

        String result = mockMvc.perform(patch("/bookings/{bookingId}", bookingToCreate.getId())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(bookingToCreate))
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(updatedBooking), result);
    }

    @SneakyThrows
    @Test
    void getAllBookingsForUser() {
        mockMvc.perform(get("/bookings")
                        .param("from", "1")
                        .param("size", "1")
                        .param("state", "ALL")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk());
        doNothing().when(pageableValidator).checkingPageableParams(1, 1);

        verify(bookingService, times(1)).getAllBookingsByUserId(1L, "ALL", PageRequest.of(1, 1));
    }

    @SneakyThrows
    @Test
    void getAllBookingsForOwner() {
        mockMvc.perform(get("/bookings/owner")
                        .param("from", "1")
                        .param("size", "1")
                        .param("state", "ALL")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk());
        doNothing().when(pageableValidator).checkingPageableParams(1, 1);

        verify(bookingService, times(1)).getAllBookingsByOwnerId(1L, "ALL", PageRequest.of(1, 1));
    }

    @SneakyThrows
    @Test
    void getInfoForBooking() {
        long bookingId = 0L;
        mockMvc.perform(get("/bookings/{bookingId}", bookingId))
                .andExpect(status().isOk());

        verify(bookingService, times(1)).getBookingInfo(anyLong(), anyLong());
    }
}