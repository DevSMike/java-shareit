package ru.practicum.shareit.item;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
@ActiveProfiles("test")
class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    UserRepository userRepository;

    private long ownerOneId;

    @BeforeEach
    public void addItems() {
        User ownerOne = User.builder()
                .email("mail1@mail.ru")
                .name("name1")
                .build();
        userRepository.save(ownerOne);

        ownerOneId = ownerOne.getId();
        itemRepository.save(Item.builder()
                .id(1L)
                .name("item1")
                .description("desc")
                .owner(ownerOne)
                .available(true)
                .build());

        User ownerTwo = User.builder()
                .email("mail2@mail.ru")
                .name("name2")
                .build();
        userRepository.save(ownerTwo);

        itemRepository.save(Item.builder()
                .id(2L)
                .name("item2")
                .description("desc")
                .owner(ownerTwo)
                .available(true)
                .build());
    }

    @Test
    void findByOwnerId_whenDataIsCorrect_thenReturnListOfFirstItem() {
        Pageable page = PageRequest.of(0, 10);
        List<Item> items = itemRepository.findByOwner_Id(ownerOneId, page);

        assertEquals(items.size(), 1);
        assertEquals(items.get(0).getName(), "item1");
    }


    @Test
    void search_whenDataIsDesc_thenReturnTwoItems() {
        Pageable page = PageRequest.of(0, 10);
        List<Item> items = itemRepository.search("dEsc", page);

        assertEquals(items.size(), 2);
    }

    @Test
    void search_whenDataIsNone_whenReturnEmptyList() {
        Pageable page = PageRequest.of(0, 10);
        List<Item> items = itemRepository.search("none", page);

        assertEquals(items.size(), 0);
    }

    @AfterEach
    public void deleteItems() {
        itemRepository.deleteAll();
    }
}