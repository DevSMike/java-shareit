package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;


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
    public String handleGatewayHeaderException(GatewayHeaderException e) {
        return String.format("Gateway exception %s", e.getMessage());
    }
}
