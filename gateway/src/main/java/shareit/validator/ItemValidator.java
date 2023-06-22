package shareit.validator;

import org.springframework.stereotype.Component;
import shareit.exception.EmptyFieldException;
import shareit.exception.IncorrectDataException;
import shareit.item.CommentDto;
import shareit.item.ItemDto;

@Component
public class ItemValidator {

    public void validateItemData(ItemDto itemDto) {
        if (itemDto.getAvailable() == null || itemDto.getDescription() == null || itemDto.getName() == null) {
            throw new EmptyFieldException("Null fields in ItemDto element!");
        }
        if (itemDto.getName().isEmpty() || itemDto.getDescription().isEmpty()) {
            throw new EmptyFieldException("Empty fields in ItemDto element!");
        }
    }

    public void validateItemDataUpdate(ItemDto itemDto) {
        if (itemDto.getAvailable() == null && itemDto.getDescription() == null && itemDto.getName() == null) {
            throw new EmptyFieldException("Null fields in ItemDto element!");
        }
    }

    public void validateCommentData(CommentDto commentDto) {
        if (commentDto.getText() == null || commentDto.getText().isEmpty()) {
            throw new IncorrectDataException("Comment text cant be empty!");
        }
    }
}
