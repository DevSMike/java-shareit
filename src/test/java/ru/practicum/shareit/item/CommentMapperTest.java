package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.comment.CommentDto;
import ru.practicum.shareit.item.dto.comment.CommentMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.comment.Comment;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.shareit.item.dto.mapper.ItemMapper.toItemDto;

class CommentMapperTest {

    private User owner;
    private Item item;

    @BeforeEach
    public void fillData() {
        owner = User.builder()
                .name("name")
                .id(1L)
                .build();
        item = Item.builder()
                .id(1L)
                .name("name")
                .owner(owner)
                .description("desc")
                .available(true)
                .build();
    }

    @Test
    void toCommentDb() {
        CommentDto commentDto = CommentDto.builder()
                .text("text")
                .created(LocalDateTime.now())
                .id(1L)
                .authorName("Bob")
                .item(toItemDto(item))
                .build();

        Comment comment = CommentMapper.toCommentDb(commentDto, owner, item);

        assertEquals(commentDto.getText(), comment.getText());
        assertEquals(comment.getAuthor().getId(), owner.getId());
        assertEquals(comment.itemId(), item.getId());
    }

    @Test
    void toCommentDto() {
        Comment comment = Comment.builder()
                .author(owner)
                .created(LocalDateTime.now())
                .item(item)
                .text("text")
                .id(1L)
                .build();

        CommentDto actual = CommentMapper.toCommentDto(comment);

        assertEquals(actual.getText(), comment.getText());
        assertEquals(actual.getItem().getId(), item.getId());
        assertEquals(actual.getAuthorName(), owner.getName());
    }
}