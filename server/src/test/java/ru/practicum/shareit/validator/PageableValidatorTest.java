package ru.practicum.shareit.validator;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.IncorrectDataException;

import static org.junit.jupiter.api.Assertions.*;

class PageableValidatorTest {

    @Test
    void checkingPageableParams_whenParamsIncorrect_thenThrowIncorrectDataException() {
        PageableValidator validator = new PageableValidator();

        IncorrectDataException exception = assertThrows(IncorrectDataException.class,
                () -> validator.checkingPageableParams(-1, -2));

        assertEquals(exception.getMessage(), "Size OR From in page can't be < 0");
    }
}