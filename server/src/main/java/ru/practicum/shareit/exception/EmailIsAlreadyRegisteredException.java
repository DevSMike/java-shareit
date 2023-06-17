package ru.practicum.shareit.exception;

public class EmailIsAlreadyRegisteredException extends RuntimeException {

    public EmailIsAlreadyRegisteredException(String message) {
        super(message);
    }

}
