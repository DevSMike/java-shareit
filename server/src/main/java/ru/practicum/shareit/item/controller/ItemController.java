package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.comment.CommentDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(@RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") long userId) {
        log.debug("Creating item element {}", itemDto);
        return itemService.create(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable long itemId, @RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") long userId) {
        log.debug("Updating item element by id {}", itemId);
        itemDto.setId(itemId);
        return itemService.update(itemDto, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable long itemId, @RequestHeader("X-Sharer-User-Id") long userId) {
        log.debug("Getting item by id : {}", itemId);
        return itemService.getItemById(itemId, userId);
    }

    @GetMapping()
    public Collection<ItemDto> getUserItems(@RequestParam(defaultValue = "0") Integer from, @RequestParam(defaultValue = "10") Integer size, @RequestHeader("X-Sharer-User-Id") long userId) {
        log.debug("Getting all items by userId {}", userId);
        Pageable page = PageRequest.of(from / size, size);
        return itemService.getItemsByUserId(userId, page);
    }

    @GetMapping("/search")
    public Collection<ItemDto> getItemsBySearch(@RequestParam(defaultValue = "0") Integer from, @RequestParam(defaultValue = "10") Integer size, @RequestParam String text) {
        log.debug("Getting items by search text: {}", text);
        Pageable page = PageRequest.of(from / size, size);
        return itemService.getItemsBySearch(text, page);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createCommentToItem(@PathVariable Long itemId, @RequestBody CommentDto comment, @RequestHeader("X-Sharer-User-Id") long userId) {
        log.debug("Creating comment to item by userId {}", userId);
        return itemService.addCommentToItem(userId, itemId, comment);
    }
}
