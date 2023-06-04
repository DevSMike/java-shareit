package ru.practicum.shareit.validator;

import ru.practicum.shareit.exception.EmptyFieldException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.IncorrectDataException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.comment.CommentDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;

public class ItemValidator {

    public static void validateItemId(long itemId, ItemRepository repository) {
        if (itemId < 0) {
            throw new IncorrectDataException("There is no item with id less than 0 : " + itemId);
        }
        repository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("There is no Item with Id: " + itemId));
    }

    public static Item validateItemIdAndReturns(long itemId, ItemRepository repository) {
        if (itemId < 0) {
            throw new IncorrectDataException("There is no item with id less than 0 : " + itemId);
        }
        return repository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("There is no Item with Id: " + itemId));
    }

    public static void validateItemData(ItemDto itemDto) {
        if (itemDto.getAvailable() == null || itemDto.getDescription() == null || itemDto.getName() == null) {
            throw new EmptyFieldException("Null fields in ItemDto element!");
        }
        if (itemDto.getName().isEmpty() || itemDto.getDescription().isEmpty()) {
            throw new EmptyFieldException("Empty fields in ItemDto element!");
        }
    }

    public static void validateCommentData(CommentDto commentDto) {
        if (commentDto.getText().isEmpty()) {
            throw new IncorrectDataException("Comment text cant be empty!");
        }
    }
}
