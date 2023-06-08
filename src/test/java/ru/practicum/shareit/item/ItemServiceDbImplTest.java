package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.EmptyFieldException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.IncorrectDataException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.comment.CommentDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.comment.Comment;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceDbImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.validator.ItemValidator;
import ru.practicum.shareit.validator.UserValidator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.item.dto.comment.CommentMapper.toCommentDto;
import static ru.practicum.shareit.item.dto.mapper.ItemMapper.toItemDto;
import static ru.practicum.shareit.item.dto.mapper.ItemMapper.toItemDtoWithRequestId;

@ExtendWith(MockitoExtension.class)
class ItemServiceDbImplTest {

    @Mock
    ItemRepository itemRepository;
    @Mock
    BookingRepository bookingRepository;
    @Mock
    CommentRepository commentRepository;
    @Mock
    ItemRequestRepository itemRequestRepository;
    @Mock
    UserValidator userValidator;
    @Mock
    ItemValidator itemValidator;

    @InjectMocks
    ItemServiceDbImpl itemService;

    @Test
    void create_whenAllDataIsCorrect_thenReturnItemWithoutRequest() {
        Item expectedItem = Item.builder()
                .available(true)
                .description("desc")
                .name("name")
                .build();
        when(userValidator.validateUserIdAndReturn(1L)).thenReturn(new User());
        when(itemRepository.save(expectedItem)).thenReturn(expectedItem);

        ItemDto actualItem = itemService.create(toItemDto(expectedItem), 1L);

        assertEquals(expectedItem.getName(), actualItem.getName());
        verify(itemValidator, times(1)).validateItemData(toItemDto(expectedItem));
    }

    @Test
    void create_whenAllDataIsCorrect_thenReturnItemWithRequest() {
        ItemRequest request = ItemRequest.builder()
                .id(1L)
                .description("need")
                .build();
        Item expectedItem = Item.builder()
                .available(true)
                .description("desc")
                .name("name")
                .request(request)
                .build();
        when(userValidator.validateUserIdAndReturn(1L)).thenReturn(new User());
        when(itemRepository.save(expectedItem)).thenReturn(expectedItem);
        when(itemRequestRepository.findById(1L)).thenReturn(Optional.of(request));

        ItemDto actualItem = itemService.create(toItemDtoWithRequestId(expectedItem), 1L);

        assertEquals(expectedItem.getName(), actualItem.getName());
        assertEquals(expectedItem.getRequest().getId(), actualItem.getRequestId());
        verify(itemValidator, times(1)).validateItemData(toItemDtoWithRequestId(expectedItem));
    }

    @Test
    void create_whenDataIsIncorrect_thenThrowEmptyFieldExceptionException() {
        Item expectedItem = new Item();
        when(itemRepository.save(expectedItem)).thenThrow(new EmptyFieldException("Null fields in ItemDto element!"));

        EmptyFieldException emptyFieldException = assertThrows(EmptyFieldException.class,
                () -> itemService.create(toItemDto(expectedItem), 1L), "exceptions are diff");
        assertEquals(emptyFieldException.getMessage(), "Null fields in ItemDto element!", "messages are diff");
    }

    @Test
    void create_whenUserNotExists_thenThrowEntityNotFoundException() {
        Item expectedItem = new Item();
        when(itemRepository.save(expectedItem))
                .thenThrow(new EntityNotFoundException("There is no User with Id: " + 1L));

        EntityNotFoundException entityNotFoundException = assertThrows(EntityNotFoundException.class,
                () -> itemService.create(toItemDto(expectedItem), 1L), "exceptions are diff");
        assertEquals(entityNotFoundException.getMessage(), "There is no User with Id: 1", "messages are diff");
    }

    @Test
    void update_whenUserAndItemExist_thenReturnItem() {
        Item expectedItem = Item.builder()
                .id(1L)
                .available(true)
                .description("desc")
                .name("name")
                .build();
        when(itemRepository.save(expectedItem)).thenReturn(expectedItem);
        when(itemValidator.validateItemIdAndReturns(1L)).thenReturn(expectedItem);

        ItemDto actualItem = itemService.update(toItemDto(expectedItem), 1L);

        assertEquals(expectedItem.getName(), actualItem.getName());
        verify(userValidator, times(1)).validateUserId(1L);
    }

    @Test
    void update_whenUserNotExist_thenThrowEntityNotFoundException() {
        Item expectedItem = new Item();
        doThrow(new EntityNotFoundException("There is no user : 1")).when(userValidator).validateUserId(1L);

        EntityNotFoundException entityNotFoundException = assertThrows(EntityNotFoundException.class,
                () -> itemService.update(toItemDto(expectedItem), 1L), "exceptions are diff");

        assertEquals(entityNotFoundException.getMessage(), "There is no user : 1", "messages are diff");
    }

    @Test
    void update_whenItemNotExists_thenThrowEntityNotFoundException() {
        long itemId = 1L;
        Item expectedItem = new Item();
        expectedItem.setId(itemId);
        doNothing().when(userValidator).validateUserId(1L);
        when(itemValidator.validateItemIdAndReturns(itemId))
                .thenThrow(new EntityNotFoundException("There is no Item with Id: " + itemId));

        EntityNotFoundException entityNotFoundException = assertThrows(EntityNotFoundException.class,
                () -> itemService.update(toItemDto(expectedItem), 1L), "exceptions are diff");

        assertEquals(entityNotFoundException.getMessage(), "There is no Item with Id: 1", "messages are diff");
    }

    @Test
    void getItemById_whenItemAndUserExists_thenReturnItem() {
        long itemId = 1L;
        long userId = 1L;
        Item expectedItem = new Item();
        expectedItem.setId(itemId);
        doNothing().when(userValidator).validateUserId(1L);
        when(itemValidator.validateItemIdAndReturns(itemId)).thenReturn(expectedItem);

        ItemDto actualItem = itemService.getItemById(itemId, userId);

        assertEquals(expectedItem.getId(), actualItem.getId());
    }

    @Test
    void getItemById_UserNotExists_thenThrowEntityNotFoundException() {
        long userId = 1L;
        long itemId = 1L;
        doThrow(new EntityNotFoundException("There is no user : 1")).when(userValidator).validateUserId(userId);

        EntityNotFoundException entityNotFoundException = assertThrows(EntityNotFoundException.class,
                () -> itemService.getItemById(itemId, userId), "exceptions are diff");

        assertEquals(entityNotFoundException.getMessage(), "There is no user : 1", "messages are diff");
    }

    @Test
    void getItemById_ItemNotExists_thenThrowEntityNotFoundException() {
        long itemId = 1L;
        long userId = 1L;
        Item expectedItem = new Item();
        expectedItem.setId(itemId);
        doNothing().when(userValidator).validateUserId(userId);
        when(itemValidator.validateItemIdAndReturns(itemId))
                .thenThrow(new EntityNotFoundException("There is no Item with Id: " + itemId));

        EntityNotFoundException entityNotFoundException = assertThrows(EntityNotFoundException.class,
                () -> itemService.getItemById(itemId, userId), "exceptions are diff");

        assertEquals(entityNotFoundException.getMessage(), "There is no Item with Id: 1", "messages are diff");
    }

    @Test
    void getItemById_whenItemAndUserExists_thenReturnItemWithBookings() {
        long itemId = 1L;
        long userId = 1L;
        User booker = new User();
        LocalDateTime nextBooking = LocalDateTime.now();
        booker.setId(userId);
        Item expectedItem = new Item();
        List<Booking> bookings = List.of(Booking.builder()
                .item(expectedItem)
                .booker(booker)
                .start(nextBooking)
                .end(null)
                .status(BookingStatus.APPROVED)
                .build());
        expectedItem.setId(itemId);
        doNothing().when(userValidator).validateUserId(userId);
        when(itemValidator.validateItemIdAndReturns(itemId)).thenReturn(expectedItem);
        when(bookingRepository.findAllByItem_Owner_Id(userId)).thenReturn(bookings);

        ItemDto actualItem = itemService.getItemById(itemId, userId);

        assertEquals(expectedItem.getId(), actualItem.getId());
        assertEquals(actualItem.getLastBooking().getBookerId(), userId);
    }

    @Test
    void getItemById_whenItemAndUserExists_thenReturnItemWithComments() {
        long itemId = 1L;
        long userId = 1L;
        User author = new User();
        author.setId(userId);
        Item expectedItem = new Item();
        List<Comment> comments = List.of(Comment.builder()
                .text("Text")
                .item(expectedItem)
                .created(LocalDateTime.now())
                .author(author)
                .build());
        expectedItem.setId(itemId);
        doNothing().when(userValidator).validateUserId(userId);
        when(itemValidator.validateItemIdAndReturns(itemId)).thenReturn(expectedItem);
        when(commentRepository.findAllByItem_Id(itemId)).thenReturn(comments);

        ItemDto actualItem = itemService.getItemById(itemId, userId);

        assertEquals(expectedItem.getId(), actualItem.getId());
        assertEquals(actualItem.getComments().size(), 1);
    }

    @Test
    void getItemById_whenItemAndUserExists_thenReturnItemWithBookingsAndComments() {
        long itemId = 1L;
        long userId = 1L;
        User booker = new User();
        LocalDateTime nextBooking = LocalDateTime.now();
        booker.setId(userId);
        Item expectedItem = new Item();
        List<Booking> bookings = List.of(Booking.builder()
                .item(expectedItem)
                .booker(booker)
                .start(nextBooking)
                .end(null)
                .status(BookingStatus.APPROVED)
                .build());
        List<Comment> comments = List.of(Comment.builder()
                .text("Text")
                .item(expectedItem)
                .created(LocalDateTime.now())
                .author(booker)
                .build());
        expectedItem.setId(itemId);
        doNothing().when(userValidator).validateUserId(userId);
        when(itemValidator.validateItemIdAndReturns(itemId)).thenReturn(expectedItem);
        when(bookingRepository.findAllByItem_Owner_Id(userId)).thenReturn(bookings);
        when(commentRepository.findAllByItem_Id(itemId)).thenReturn(comments);

        ItemDto actualItem = itemService.getItemById(itemId, userId);

        assertEquals(expectedItem.getId(), actualItem.getId());
        assertEquals(actualItem.getComments().size(), 1);
        assertEquals(actualItem.getLastBooking().getBookerId(), userId);
    }

    @Test
    void getItemsByUserId_whenUserExists_thenReturnUserItems() {
        long userId = 1L;
        Pageable pageForItems = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id"));
        doNothing().when(userValidator).validateUserId(userId);
        when(itemRepository.findByOwner_Id(userId, pageForItems)).thenReturn(List.of(new Item()));

        Collection<ItemDto> userItems = itemService.getItemsByUserId(userId, pageForItems);

        assertEquals(userItems.size(), 1);
    }

    @Test
    void getItemsByUserId_whenUserNotExists_thenThrowEntityNotFoundException() {
        long userId = 1L;
        Pageable pageForItems = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id"));
        doThrow(new EntityNotFoundException("There is no user : 1")).when(userValidator).validateUserId(userId);

        EntityNotFoundException entityNotFoundException = assertThrows(EntityNotFoundException.class,
                () -> itemService.getItemsByUserId(userId, pageForItems), "exceptions are diff");

        assertEquals(entityNotFoundException.getMessage(), "There is no user : 1", "messages are diff");
    }

    @Test
    void getItemsByUserId_whenUserExists_thenReturnUserItemsWithComments() {
        long userId = 1L;
        Item itemToComment = new Item();
        itemToComment.setId(1L);
        Pageable pageForItems = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id"));
        Pageable pageForComments = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "created"));
        List<Comment> comments = List.of(Comment.builder()
                .text("Text")
                .item(itemToComment)
                .created(LocalDateTime.now())
                .build());
        doNothing().when(userValidator).validateUserId(userId);
        when(itemRepository.findByOwner_Id(userId, pageForItems)).thenReturn(List.of(itemToComment));
        when(commentRepository.findAllByItemsUserId(userId, pageForComments)).thenReturn(comments);

        Collection<ItemDto> userItems = itemService.getItemsByUserId(userId, pageForItems);
        List<ItemDto> items = new ArrayList<>(userItems);

        assertEquals(userItems.size(), 1);
        assertEquals(items.get(0).getComments().size(), 1);
    }

    @Test
    void getItemsByUserId_whenUserExists_thenReturnUserItemsWithBookings() {
        long userId = 1L;
        Item itemToBooking = new Item();
        itemToBooking.setId(1L);
        LocalDateTime start = LocalDateTime.now();
        Pageable pageForItems = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id"));
        List<Booking> bookings = List.of(Booking.builder()
                .item(itemToBooking)
                .start(start)
                .end(null)
                .status(BookingStatus.APPROVED)
                .build());
        doNothing().when(userValidator).validateUserId(userId);
        when(itemRepository.findByOwner_Id(userId, pageForItems)).thenReturn(List.of(itemToBooking));
        when(bookingRepository.findAllByItem_Owner_Id(userId)).thenReturn(bookings);

        Collection<ItemDto> userItems = itemService.getItemsByUserId(userId, pageForItems);
        List<ItemDto> items = new ArrayList<>(userItems);

        assertEquals(userItems.size(), 1);
        assertEquals(items.get(0).getLastBooking().getStart(), start);
    }

    @Test
    void getItemsByUserId_whenUserExists_thenReturnUserItemsWithBookingsAndComments() {
        long userId = 1L;
        Item itemToBooking = new Item();
        itemToBooking.setId(1L);
        LocalDateTime start = LocalDateTime.now();
        Pageable pageForItems = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id"));
        List<Booking> bookings = List.of(Booking.builder()
                .item(itemToBooking)
                .start(start)
                .end(null)
                .status(BookingStatus.APPROVED)
                .build());
        Pageable pageForComments = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "created"));
        List<Comment> comments = List.of(Comment.builder()
                .text("Text")
                .item(itemToBooking)
                .created(LocalDateTime.now())
                .build());
        doNothing().when(userValidator).validateUserId(userId);
        when(itemRepository.findByOwner_Id(userId, pageForItems)).thenReturn(List.of(itemToBooking));
        when(bookingRepository.findAllByItem_Owner_Id(userId)).thenReturn(bookings);
        when(commentRepository.findAllByItemsUserId(userId, pageForComments)).thenReturn(comments);

        Collection<ItemDto> userItems = itemService.getItemsByUserId(userId, pageForItems);
        List<ItemDto> items = new ArrayList<>(userItems);

        assertEquals(userItems.size(), 1);
        assertEquals(items.get(0).getLastBooking().getStart(), start);
        assertEquals(items.get(0).getComments().size(), 1);
    }

    @Test
    void getItemsBySearch_whenItemsAvailableTrue_thenReturnItems() {
        Item item = Item.builder()
                .name("name")
                .description("desc")
                .available(true)
                .build();
        Pageable page = PageRequest.of(0, 10);
        String search = "DesC";
        when(itemRepository.search("DesC", page)).thenReturn(List.of(item));

        Collection<ItemDto> items = itemService.getItemsBySearch(search, page);
        List<ItemDto> itemsList = new ArrayList<>(items);

        assertEquals(itemsList.size(), 1);
        assertEquals(itemsList.get(0).getName(), "name");
    }

    @Test
    void getItemsBySearch_whenItemsAvailableFalse_thenReturnEmptyList() {
        Pageable page = PageRequest.of(0, 10);
        String search = "DesC";
        when(itemRepository.search("DesC", page)).thenReturn(new ArrayList<>());

        Collection<ItemDto> items = itemService.getItemsBySearch(search, page);
        List<ItemDto> itemsList = new ArrayList<>(items);

        assertEquals(itemsList.size(), 0);
    }


    @Test
    void addCommentToItem_whenItemAndUserExistAndCommentDataCorrect_thenReturnComment() {
        long userId = 1L;
        long itemId = 1L;
        List<Booking> bookings = List.of(Booking.builder()
                .item(null)
                .start(null)
                .end(null)
                .status(BookingStatus.APPROVED)
                .build());
        Comment expectedComment = Comment.builder().id(1L).text("text").build();
        doNothing().when(userValidator).validateUserId(userId);
        doNothing().when(itemValidator).validateItemId(itemId);
        when(bookingRepository.findAllByUserIdAndItemIdAndEndDateIsPassed(any(Long.class), any(Long.class), any(LocalDateTime.class)))
                .thenReturn(bookings);
        when(commentRepository.save(any(Comment.class))).thenReturn(expectedComment);
        when(itemValidator.validateItemIdAndReturns(itemId)).thenReturn(new Item());

        CommentDto actualComment = itemService.addCommentToItem(userId, itemId, toCommentDto(expectedComment));

        assertEquals(toCommentDto(expectedComment), actualComment);
    }

    @Test
    void addCommentToItem_whenItemAndUserExistAndCommentDataInCorrect_thenThrowIncorrectDataException() {
        long userId = 1L;
        long itemId = 1L;
        Comment expectedComment = Comment.builder().build();
        doThrow(new IncorrectDataException("Comment text cant be empty!")).when(itemValidator)
                .validateCommentData(toCommentDto(expectedComment));

        IncorrectDataException incorrectDataException = assertThrows(IncorrectDataException.class,
                () -> itemService.addCommentToItem(userId, itemId, toCommentDto(expectedComment)));

        assertEquals(incorrectDataException.getMessage(), "Comment text cant be empty!", "messages are diff");
    }

    @Test
    void addCommentToItem_whenUserNotExists_thenThrowEntityNotFoundException() {
        long userId = 1L;
        long itemId = 1L;
        Comment expectedComment = Comment.builder().build();
        when(userValidator.validateUserIdAndReturn(userId)).thenThrow(new EntityNotFoundException("There is no user : 1"));

        EntityNotFoundException entityNotFoundException = assertThrows(EntityNotFoundException.class,
                () -> itemService.addCommentToItem(userId, itemId, toCommentDto(expectedComment)));

        assertEquals(entityNotFoundException.getMessage(), "There is no user : 1", "messages are diff");
    }

    @Test
    void addCommentToItem_whenItemNotExists_thenThrowEntityNotFoundException() {
        long userId = 1L;
        long itemId = 1L;
        Comment expectedComment = Comment.builder().build();
        doThrow(new EntityNotFoundException("There is no Item with Id: 1")).when(itemValidator).validateItemId(itemId);

        EntityNotFoundException entityNotFoundException = assertThrows(EntityNotFoundException.class,
                () -> itemService.addCommentToItem(userId, itemId, toCommentDto(expectedComment)));

        assertEquals(entityNotFoundException.getMessage(), "There is no Item with Id: 1", "messages are diff");
    }

    @Test
    void addCommentToItem_whenUserBookingsAreEmpty_thenThrowIncorrectDataException() {
        long userId = 1L;
        long itemId = 1L;
        Comment expectedComment = Comment.builder().id(1L).text("text").created(LocalDateTime.now()).build();
        when(bookingRepository.findAllByUserIdAndItemIdAndEndDateIsPassed(any(Long.class), any(Long.class), any(LocalDateTime.class)))
                .thenReturn(new ArrayList<>());

        IncorrectDataException incorrectDataException = assertThrows(IncorrectDataException.class,
                () -> itemService.addCommentToItem(userId, itemId, toCommentDto(expectedComment)));

        assertEquals(incorrectDataException.getMessage(), "This user has no booking", "messages are diff");
    }

}