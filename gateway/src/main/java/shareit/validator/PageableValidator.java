package shareit.validator;

import org.springframework.stereotype.Component;
import shareit.exception.IncorrectDataException;

@Component
public class PageableValidator {

    public void checkingPageableParams(Integer from, Integer size) {
        if (size < 0 || from < 0) {
            throw new IncorrectDataException("Size OR From in page can't be < 0");
        }
    }
}
