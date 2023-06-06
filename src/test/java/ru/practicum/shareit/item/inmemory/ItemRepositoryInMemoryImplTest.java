package ru.practicum.shareit.item.inmemory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.inmemmory.ItemRepositoryInMemoryImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.inmemory.UserRepositoryInMemoryImpl;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRepositoryInMemoryImplTest {

    private Item expectedItem;
    private User user;

    @Mock
    UserRepositoryInMemoryImpl userRepositoryInMemory;

    @InjectMocks
    ItemRepositoryInMemoryImpl itemRepositoryInMemory;

    @BeforeEach
    public void fillData() {
        expectedItem = Item.builder()
                .id(1L)
                .available(true)
                .description("desc")
                .name("name")
                .owner(User.builder().id(1L).build())
                .build();

        user = User.builder()
                .id(1L)
                .email("mail@mail.ru")
                .name("name")
                .build();
    }

    @Test
    void create_whenAllDataIsCorrect_thenReturnItem() {
        when(userRepositoryInMemory.getById(anyLong())).thenReturn(new User());

        Item actual = itemRepositoryInMemory.create(expectedItem, 1L);

        assertEquals(actual.getId(), 1);
        assertEquals(actual.getAvailable(), expectedItem.getAvailable());
    }

    @Test
    void update_whenItemExists_thenReturnUpdated() {
        when(userRepositoryInMemory.getById(anyLong())).thenReturn(user);
        Item itemFromMap = itemRepositoryInMemory.create(expectedItem, 1L);
        itemFromMap.setAvailable(false);

        Item actual = itemRepositoryInMemory.update(itemFromMap, user.getId());

        assertEquals(actual.getAvailable(), false);
    }

    @Test
    void getItemById_whenItemExists_thenReturnItem() {
        Item itemFromMap = itemRepositoryInMemory.create(expectedItem, 1L);

        Item itemGetById = itemRepositoryInMemory.getItemById(itemFromMap.getId());

        assertEquals(itemFromMap.getName(), itemGetById.getName());
    }

    @Test
    void getItemsByUserId() {
        itemRepositoryInMemory.create(expectedItem, 1L);

        List<Item> userItems = new ArrayList<>(itemRepositoryInMemory.getItemsByUserId(1L));

        assertEquals(userItems.size(), 1);
    }

    @Test
    void getItemsBySearch() {
        itemRepositoryInMemory.create(expectedItem, 1L);

        List<Item> itemsBySearch = new ArrayList<>(itemRepositoryInMemory.getItemsBySearch("desc"));

        assertEquals(itemsBySearch.size(), 1);
    }

}