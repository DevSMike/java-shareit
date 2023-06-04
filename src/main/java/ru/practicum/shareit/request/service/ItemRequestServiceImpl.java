package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.IncorrectDataException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.stream.Collectors;

import static ru.practicum.shareit.request.dto.mapper.ItemRequestMapper.*;
import static ru.practicum.shareit.user.dto.mapper.UserMapper.toUser;
import static ru.practicum.shareit.user.dto.mapper.UserMapper.toUserDto;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;

    @Override
    public ItemRequestDto addNewRequest(ItemRequestDto requestDto, Long userId) {
        UserDto requester = checkingUserId(userId);
        if (requestDto.getDescription() == null || requestDto.getDescription().isEmpty()) {
            throw new IncorrectDataException("Item request description can't be null!");
        }
        return toItemRequestDto(itemRequestRepository.save(toItemRequest(requestDto, toUser(requester))));
    }

    @Override
    public Collection<ItemRequestDto> getAllUserRequestsWithResponses(Long userId) {
        checkingUserId(userId);
        return itemRequestRepository.findAllByRequester_Id(userId).stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemRequestDto> getAllRequestsToResponse(Long userId, Pageable page) {
        checkingUserId(userId);
        return itemRequestRepository.findAllByAllOtherUsers(userId, page).stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getRequestById(Long userId, Long requestId) {
        checkingUserId(userId);
        return toItemRequestDto(itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("ItemRequest with id: " + requestId + " not found!")));
    }


    private UserDto checkingUserId(Long userId) {
        if (userId == -1) {
            throw new IncorrectDataException("There is no user with header-Id : " + userId);
        }
        return toUserDto(userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("There is no user with id: " + userId)));
    }
}
