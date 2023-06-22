package ru.practicum.shareit.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.EmptyFieldException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.IncorrectDataException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.comment.CommentDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;

@Component
@RequiredArgsConstructor
public class ItemValidator {

    private final ItemRepository repository;

    public void validateItemId(long itemId) {
        if (itemId < 0) {
            throw new IncorrectDataException("There is no item with id less than 0 : " + itemId);
        }
        repository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("There is no Item with Id: " + itemId));
    }

    public Item validateItemIdAndReturns(long itemId) {
        if (itemId < 0) {
            throw new IncorrectDataException("There is no item with id less than 0 : " + itemId);
        }
        return repository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("There is no Item with Id: " + itemId));
    }

    public void validateItemData(ItemDto itemDto) {
        if (itemDto.getAvailable() == null || itemDto.getDescription() == null || itemDto.getName() == null) {
            throw new EmptyFieldException("Null fields in ItemDto element!");
        }
        if (itemDto.getName().isEmpty() || itemDto.getDescription().isEmpty()) {
            throw new EmptyFieldException("Empty fields in ItemDto element!");
        }
    }

    public void validateCommentData(CommentDto commentDto) {
        if (commentDto.getText() == null || commentDto.getText().isEmpty()) {
            throw new IncorrectDataException("Comment text cant be empty!");
        }
    }
}
