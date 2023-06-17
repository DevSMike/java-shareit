package ru.practicum.shareit.request;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class ItemRequestRepositoryTest {
    private Long userOneId;

    @Autowired
    ItemRequestRepository itemRequestRepository;

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    public void addRequests() {
        User userOne = User.builder()
                .email("mail1@mail.ru")
                .name("name1")
                .build();
        userRepository.save(userOne);
        userOneId = userOne.getId();

        itemRequestRepository.save(ItemRequest.builder()
                        .creationDate(LocalDateTime.now())
                        .requester(userOne)
                        .description("text")
                        .build());
        User userTwo = User.builder()
                .email("mail2@mail.ru")
                .name("name2")
                .build();
        userRepository.save(userTwo);

        itemRequestRepository.save(ItemRequest.builder()
                .creationDate(LocalDateTime.now())
                .requester(userTwo)
                .description("text-2")
                .build());
    }

    @Test
    void findAllByRequesterId_whenRequesterUserOne_thenReturnListOfUserOneRequest() {
        List<ItemRequest> requestList = new ArrayList<>(itemRequestRepository.findAllByRequester_Id(userOneId));

        assertEquals(requestList.size(), 1);
        assertEquals(requestList.get(0).getRequester().getName(), "name1");

    }

    @Test
    void findAllByAllOtherUsers_whenUserIdIsUserOneId_thenReturnListOfUserTwoRequest() {
        Pageable page = PageRequest.of(0, 10);
        List<ItemRequest> requestList = new ArrayList<>(itemRequestRepository.findAllByAllOtherUsers(userOneId,page));

        assertEquals(requestList.size(), 1);
        assertEquals(requestList.get(0).getRequester().getName(), "name2");
    }

    @AfterEach
    public void deleteItems() {
        itemRequestRepository.deleteAll();
    }
}