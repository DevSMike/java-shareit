package ru.practicum.shareit.validator;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.IncorrectDataException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;

@Component
@RequiredArgsConstructor
public class ItemRequestValidator {

    private final ItemRequestRepository repository;

    public void validateItemRequestId(long requestId) {
        if (requestId < 0) {
            throw new IncorrectDataException("There is no request with id less than 0 : " + requestId);
        }
        repository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("There is no request with id: " + requestId));
    }

    public ItemRequest validateItemRequestIdAndReturns(long requestId) {
        if (requestId < 0) {
            throw new IncorrectDataException("There is no request with id less than 0 : " + requestId);
        }
        return repository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("There is no request with id: " + requestId));
    }

    public void validateItemRequestData(ItemRequestDto requestDto) {
        if (requestDto.getDescription() == null || requestDto.getDescription().isEmpty()) {
            throw new IncorrectDataException("Item request description can't be null!");
        }
    }
}
