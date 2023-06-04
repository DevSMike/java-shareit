package ru.practicum.shareit.validator;

import ru.practicum.shareit.exception.IncorrectDataException;

public class PageableValidator {

    public static void checkingPageableParams(Integer from, Integer size) {
        if (size < 0 || from < 0) {
            throw new IncorrectDataException("Size OR From in page can't be < 0");
        }
    }
}
