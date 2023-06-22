package ru.practicum.shareit.item.dto.comment;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.comment.Comment;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import static ru.practicum.shareit.item.dto.mapper.ItemMapper.toItemDto;
import static ru.practicum.shareit.user.dto.mapper.UserMapper.toUserDto;

public class CommentMapper {

    public static Comment toCommentDb(CommentDto commentDto, User author, Item item) {
        return Comment.builder()
                .id(commentDto.getId() != null ? commentDto.getId() : 0L)
                .author(author)
                .item(item)
                .text(commentDto.getText())
                .created(commentDto.getCreated())
                .build();
    }

    public static CommentDto toCommentDto(Comment comment) {
        CommentDto commentDto = CommentDto.builder()
                .id(comment.getId())
                .created(comment.getCreated())
                .text(comment.getText())
                .build();
        if (comment.getItem() != null) {
            ItemDto itemDto = toItemDto(comment.getItem());
            commentDto.setItem(itemDto);
        }
        if (comment.getAuthor() != null) {
            UserDto userDto = toUserDto(comment.getAuthor());
            commentDto.setAuthorName(userDto.getName());
        }
        return commentDto;
    }
}
