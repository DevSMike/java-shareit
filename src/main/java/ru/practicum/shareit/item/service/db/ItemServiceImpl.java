package ru.practicum.shareit.item.service.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.mapper.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepositoryDb;
import ru.practicum.shareit.exception.EmptyFieldException;
import ru.practicum.shareit.exception.EntityNotFoundException;

import ru.practicum.shareit.exception.IncorrectDataException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.comment.CommentDto;
import ru.practicum.shareit.item.dto.comment.CommentMapper;
import ru.practicum.shareit.item.dto.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepositoryDb;
import ru.practicum.shareit.item.repository.ItemRepositoryDb;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.item.dto.comment.CommentMapper.toCommentDto;
import static ru.practicum.shareit.item.dto.mapper.ItemMapper.*;
import static ru.practicum.shareit.item.dto.mapper.ItemMapper.toItemDto;
import static ru.practicum.shareit.user.dto.mapper.UserMapper.toUser;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final UserService userService;
    private final ItemRepositoryDb itemRepository;
    private final BookingRepositoryDb bookingRepository;
    private final CommentRepositoryDb commentRepository;


    @Override
    public ItemDto create(ItemDto itemDto, long userId) {
        if (itemDto.getAvailable() == null || itemDto.getDescription() == null || itemDto.getName() == null) {
            throw new EmptyFieldException("Null fields in ItemDto element!");
        }
        if (itemDto.getName().isEmpty() || itemDto.getDescription().isEmpty()) {
            throw new EmptyFieldException("Empty fields in ItemDto element!");
        }
        UserDto userFromDb = checkingUserId(userId);
        log.debug("Creating item element : {}; for user {}", itemDto, userId);
        return toItemDto(itemRepository.save(toItemDb(itemDto, toUser(userFromDb))));
    }

    @Override
    public ItemDto update(ItemDto itemDto, long userId) {
        checkingUserId(userId);
        log.debug("Updating item element : {}; for user {}", itemDto, userId);
        Item itemToUpdate = toItemUpdate(itemDto, itemRepository.findById(itemDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("There is no Item with Id: " + itemDto.getId())));
        itemRepository.update(itemToUpdate.getName(), itemToUpdate.getDescription(),
                itemToUpdate.getAvailable(), itemToUpdate.getId());
        return toItemDto(itemToUpdate);
    }

    @Override
    public ItemDto getItemById(long itemId, long userId) {
        UserDto userFromDb = checkingUserId(userId);
        List<CommentDto> commentsForItem = commentRepository.findAllByItem_Id(itemId)
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
        try {
            checkItemOwner(itemId, userId);
            List<BookingDto> ownerBooking = getOwnerBooking(userId)
                    .stream()
                    .filter(x -> x.getItem().getId().equals(itemId))
                    .collect(Collectors.toList());
            if (ownerBooking.isEmpty()) {
                throw new EntityNotFoundException("Owner Booking is null!");
            }
            if (!commentsForItem.isEmpty()) {
                return toItemDtoWithBookingsAndComments(itemRepository.findById(itemId)
                        .orElseThrow(() -> new EntityNotFoundException("There is no Item with Id: " + itemId)), ownerBooking, commentsForItem);
            }
            return toItemDtoWithBookings(itemRepository.findById(itemId)
                    .orElseThrow(() -> new EntityNotFoundException("There is no Item with Id: " + itemId)), ownerBooking);
        } catch (EntityNotFoundException e) {
            log.info("Only owner can get info with bookings!");
        }
        log.debug("Getting item element by id : {}; for user {}", itemId, userFromDb.getId());
        if (!commentsForItem.isEmpty()) {
            return toItemDtoWithComments(itemRepository.findById(itemId)
                    .orElseThrow(() -> new EntityNotFoundException("There is no Item with Id: " + itemId)), commentsForItem);
        }
        return toItemDto(itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("There is no Item with Id: " + itemId)));
    }

    @Override
    public Collection<ItemDto> getItemsByUserId(long userId) {
        UserDto userFromDb = checkingUserId(userId);
        List<CommentDto> commentsForItem = commentRepository.findAllByItemsUserId(userId)
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
        log.debug("Getting items by user Id : {} ", userFromDb.getId());
        List<BookingDto> ownerBookings = getOwnerBooking(userId);
        if (ownerBookings.isEmpty()) {
            if (!commentsForItem.isEmpty()) {
                return itemRepository.findByOwner_Id(userFromDb.getId(), Sort.by(Sort.Direction.ASC, "id"))
                        .stream()
                        .map(x -> toItemDtoWithComments(x, commentsForItem.stream()
                                .filter(y -> y.getItem().getId().equals(x.getId()))
                                .collect(Collectors.toList())))
                        .collect(Collectors.toList());
            }
            return itemRepository.findByOwner_Id(userFromDb.getId(), Sort.by(Sort.Direction.ASC, "id"))
                    .stream()
                    .map(ItemMapper::toItemDto)
                    .collect(Collectors.toList());
        } else {
            if (!commentsForItem.isEmpty()) {
                return itemRepository.findByOwner_Id(userFromDb.getId(), Sort.by(Sort.Direction.ASC, "id"))
                        .stream()
                        .map(x -> toItemDtoWithBookingsAndComments(x, ownerBookings.stream()
                                .filter(y -> y.getItem().getId().equals(x.getId()))
                                .collect(Collectors.toList()), commentsForItem.stream()
                                .filter(z -> z.getItem().getId().equals(x.getId()))
                                .collect(Collectors.toList())))
                        .collect(Collectors.toList());
            }
            return itemRepository.findByOwner_Id(userFromDb.getId(), Sort.by(Sort.Direction.ASC, "id"))
                    .stream()
                    .map(x -> toItemDtoWithBookings(x, ownerBookings.stream()
                            .filter(y -> y.getItem().getId().equals(x.getId()))
                            .collect(Collectors.toList())))
                    .collect(Collectors.toList());
        }

    }

    @Override
    public Collection<ItemDto> getItemsBySearch(String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        log.debug("Getting items by search : {} ", text);
        return itemRepository.search(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto checkItemOwner(Long itemId, Long ownerId) {
        long realOwnerId = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("There is no Item with Id: " + itemId)).getOwner().getId();
        if (ownerId.equals(realOwnerId)) {
            return toItemDto(itemRepository.findById(itemId).get());
        } else {
            throw new EntityNotFoundException("This user is not owner!");
        }
    }

    @Override
    public CommentDto addCommentToItem(Long userId, Long itemId, CommentDto commentDto) {
        if (commentDto.getText().isEmpty()) {
            throw new IncorrectDataException("Comment text cant be empty!");
        }
        UserDto author = checkingUserId(userId);
        List<BookingDto> bookings = bookingRepository.findAllByUserIdAndItemIdAndEndDateIsPassed(userId, itemId, LocalDateTime.now())
                .stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
        if (bookings.isEmpty()) {
            throw new IncorrectDataException("This user has no booking");
        }
        ItemDto item = getItemById(itemId, userId);
        commentDto = toCommentDto(commentRepository.save(CommentMapper.toCommentDb(commentDto, toUser(author), toItem(item))));
        return commentDto;
    }

    private UserDto checkingUserId(long userId) {
        if (userId == -1) {
            throw new IncorrectDataException("There is no user with header-Id : " + userId);
        }
        return userService.getById(userId);
    }

    private List<BookingDto> getOwnerBooking(Long ownerId) {
        return bookingRepository.findAllByOwnerId(ownerId)
                .stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }
}