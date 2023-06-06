package ru.practicum.shareit.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ErrorHandlerTest {

    private ErrorHandler errorHandler;

    @BeforeEach
    public void setUp() {
        errorHandler = new ErrorHandler();
    }

    @Test
    public void testHandleUserNotFoundException() {
        EntityNotFoundException exception = new EntityNotFoundException("User not found");
        String result = errorHandler.handleUserNotFoundException(exception);
        assertEquals("Entity not found error: User not found", result);
    }

    @Test
    public void testHandleEmailAlreadyExistsException() {
        EmailIsAlreadyRegisteredException exception = new EmailIsAlreadyRegisteredException("Email already exists");
        String result = errorHandler.handleEmailAlreadyExistsException(exception);
        assertEquals("Email is already registered Email already exists", result);
    }

    @Test
    public void testHandleEmptyFieldException() {
        EmptyFieldException exception = new EmptyFieldException("Field is empty");
        String result = errorHandler.handleEmptyFieldException(exception);
        assertEquals("Empty field exception: Field is empty", result);
    }

    @Test
    public void testHandleGatewayHeaderException() {
        IncorrectDataException exception = new IncorrectDataException("Invalid data");
        String result = errorHandler.handleGatewayHeaderException(exception);
        assertEquals("Gateway exception Invalid data", result);
    }

    @Test
    public void testHandleUnsupportedStateException() {
        UnsupportedStatusException exception = new UnsupportedStatusException("Invalid status");
        Map<String, String> result = errorHandler.handleUnsupportedStateException(exception);
        assertEquals("Unknown state: Invalid status", result.get("error"));
    }

    @Test
    public void testHandleUnsupportedMethodException() {
        UnsupportedMethodException exception = new UnsupportedMethodException("method");
        String result = errorHandler.handleUnsupportedMethodException(exception);
        assertEquals("Unsupported method: method", result);
    }
}
