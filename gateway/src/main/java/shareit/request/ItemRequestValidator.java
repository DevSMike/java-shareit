package shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import shareit.exception.IncorrectDataException;


@Component
@RequiredArgsConstructor
public class ItemRequestValidator {

    public void validateItemRequestData(ItemRequestDto requestDto) {
        if (requestDto.getDescription() == null || requestDto.getDescription().isEmpty()) {
            throw new IncorrectDataException("Item request description can't be null!");
        }
    }
}
