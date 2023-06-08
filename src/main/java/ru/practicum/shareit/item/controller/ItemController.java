package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.comment.CommentDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.validator.PageableValidator;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;
    private final PageableValidator pageableValidator;

    @PostMapping
    public ItemDto createItem(@RequestBody ItemDto itemDto, HttpServletRequest request) {
        log.debug("Creating item element {}", itemDto);
        return itemService.create(itemDto, request.getIntHeader("X-Sharer-User-Id"));
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable long itemId, @RequestBody ItemDto itemDto, HttpServletRequest request) {
        log.debug("Updating item element by id {}", itemId);
        itemDto.setId(itemId);
        return itemService.update(itemDto, request.getIntHeader("X-Sharer-User-Id"));
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable long itemId, HttpServletRequest request) {
        log.debug("Getting item by id : {}", itemId);
        return itemService.getItemById(itemId, request.getIntHeader("X-Sharer-User-Id"));
    }

    @GetMapping()
    public Collection<ItemDto> getUserItems(@RequestParam(defaultValue = "0") Integer from, @RequestParam(defaultValue = "10") Integer size, HttpServletRequest request) {
        pageableValidator.checkingPageableParams(from, size);
        log.debug("Getting all items by userId {}", request.getIntHeader("X-Sharer-User-Id"));
        Pageable page = PageRequest.of(from / size, size);
        return itemService.getItemsByUserId(request.getIntHeader("X-Sharer-User-Id"), page);
    }

    @GetMapping("/search")
    public Collection<ItemDto> getItemsBySearch(@RequestParam(defaultValue = "0") Integer from, @RequestParam(defaultValue = "10") Integer size, @RequestParam String text) {
        pageableValidator.checkingPageableParams(from, size);
        log.debug("Getting items by search text: {}", text);
        Pageable page = PageRequest.of(from / size, size);
        return itemService.getItemsBySearch(text, page);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createCommentToItem(@PathVariable Long itemId, @RequestBody CommentDto comment, HttpServletRequest request) {
        log.debug("Creating comment to item by userId {}", request.getIntHeader("X-Sharer-User-Id"));
        return itemService.addCommentToItem((long) request.getIntHeader("X-Sharer-User-Id"), itemId, comment);
    }
}
