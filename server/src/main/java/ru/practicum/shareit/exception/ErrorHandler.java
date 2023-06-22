package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;


@RestControllerAdvice()
public class ErrorHandler {

    @ExceptionHandler()
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleUserNotFoundException(EntityNotFoundException e) {
        return String.format("Entity not found error: %s", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleEmailAlreadyExistsException(EmailIsAlreadyRegisteredException e) {
        return String.format("Email is already registered %s", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleEmptyFieldException(EmptyFieldException e) {
        return String.format("Empty field exception: %s", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleGatewayHeaderException(IncorrectDataException e) {
        return String.format("Gateway exception %s", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String,String> handleUnsupportedStateException(UnsupportedStatusException e) {
        return Map.of("error",  "Unknown state: " + e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleUnsupportedMethodException(UnsupportedMethodException e) {
        return String.format("Unsupported method: %s", e.getMessage());
    }
}
