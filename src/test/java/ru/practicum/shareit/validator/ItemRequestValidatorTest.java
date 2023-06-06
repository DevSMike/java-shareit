package ru.practicum.shareit.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.IncorrectDataException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRequestValidatorTest {

    @Mock
    ItemRequestRepository itemRequestRepository;

    @InjectMocks
    ItemRequestValidator itemRequestValidator;

    @Test
    void validateItemRequestId_whenRequestNotExists_thenThrowEntityNotFoundException() {
        when(itemRequestRepository.findById(anyLong())).thenThrow(new EntityNotFoundException("Request not found"));

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> itemRequestValidator.validateItemRequestId(1L));

        assertEquals(exception.getMessage(), "Request not found");
    }


    @Test
    void validateItemRequestId_whenRequestIdLessThanZero_thenThrowIncorrectDataException() {
        IncorrectDataException exception = assertThrows(IncorrectDataException.class,
                () -> itemRequestValidator.validateItemRequestId(-1L));

        assertEquals(exception.getMessage(), "There is no request with id less than 0 : -1");
    }

    @Test
    void validateItemRequestIdAndReturns_whenRequestExists_thenReturnRequest() {
        ItemRequest request = new ItemRequest();
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(request));

        ItemRequest actual =  itemRequestValidator.validateItemRequestIdAndReturns(1L);

        assertEquals(actual, request);
    }

    @Test
    void validateItemRequestIdAndReturns_whenRequestNotExists_thenThrowEntityNotFoundException() {
        when(itemRequestRepository.findById(anyLong())).thenThrow(new EntityNotFoundException("Request not found"));

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> itemRequestValidator.validateItemRequestIdAndReturns(1L));

        assertEquals(exception.getMessage(), "Request not found");
    }

    @Test
    void validateItemRequestIdAndReturns_whenRequestIdLessThanZero_thenThrowIncorrectDataException() {
        IncorrectDataException exception = assertThrows(IncorrectDataException.class,
                () -> itemRequestValidator.validateItemRequestIdAndReturns(-1L));

        assertEquals(exception.getMessage(), "There is no request with id less than 0 : -1");
    }

    @Test
    void validateItemRequestData_whenDataIncorrect_throwIncorrectDataException() {
        IncorrectDataException exception = assertThrows(IncorrectDataException.class,
                () -> itemRequestValidator.validateItemRequestData(new ItemRequestDto()));

        assertEquals(exception.getMessage(), "Item request description can't be null!");
    }
}

