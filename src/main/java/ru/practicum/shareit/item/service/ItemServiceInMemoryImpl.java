package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.EmptyFieldException;
import ru.practicum.shareit.exception.UnsupportedMethodException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.comment.CommentDto;
import ru.practicum.shareit.item.dto.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.inmemmory.ItemRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import static ru.practicum.shareit.item.dto.mapper.ItemMapper.*;

@Slf4j
@RequiredArgsConstructor
public class ItemServiceInMemoryImpl implements ItemService {

    private final ItemRepository itemRepository;

    @Override
    public ItemDto create(ItemDto itemDto, long userId) {
        if (itemDto.getAvailable() == null || itemDto.getDescription() == null || itemDto.getName() == null) {
            throw new EmptyFieldException("Null fields in ItemDto element!");
        }
        if (itemDto.getName().isEmpty() || itemDto.getDescription().isEmpty()) {
            throw new EmptyFieldException("Empty fields in ItemDto element!");
        }
        log.debug("Creating item element : {}; for user {}", itemDto, userId);
        return toItemDto(itemRepository.create(toItem(itemDto), userId));
    }

    @Override
    public ItemDto update(ItemDto itemDto, long userId) {
        log.debug("Updating item element : {}; for user {}", itemDto, userId);
        return toItemDto(itemRepository.update(toItemUpdate(itemDto, itemRepository
                .getItemById(itemDto.getId())), userId));
    }

    @Override
    public ItemDto getItemById(long itemId, long userId) {
        log.debug("Getting item element by id : {}; for user {}", itemId, userId);
        return toItemDto(itemRepository.getItemById(itemId));
    }

    @Override
    public Collection<ItemDto> getItemsByUserId(long userId, Pageable page) {
        log.debug("Getting items by user Id : {} ", userId);
        return itemRepository.getItemsByUserId(userId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemDto> getItemsBySearch(String text, Pageable page) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        log.debug("Getting items by search : {} ", text);
        return itemRepository.getItemsBySearch(text.toLowerCase()).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto checkItemOwner(Long itemId, Long ownerId) {
        throw new UnsupportedMethodException("inMemory checkItemOwner");
    }

    @Override
    public CommentDto addCommentToItem(Long userId, Long itemId, CommentDto commentDto) {
        throw new UnsupportedMethodException("inMemory addCommentToItem");
    }

}
