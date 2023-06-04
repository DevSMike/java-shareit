package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;


import java.util.Collection;
import java.util.stream.Collectors;

import static ru.practicum.shareit.request.dto.mapper.ItemRequestMapper.*;
import static ru.practicum.shareit.validator.ItemRequestValidator.validateItemRequestData;
import static ru.practicum.shareit.validator.ItemRequestValidator.validateItemRequestIdAndReturns;
import static ru.practicum.shareit.validator.UserValidator.validateUserId;
import static ru.practicum.shareit.validator.UserValidator.validateUserIdAndReturn;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;

    @Override
    public ItemRequestDto addNewRequest(ItemRequestDto requestDto, Long userId) {
        User requester = validateUserIdAndReturn(userId, userRepository);
        validateItemRequestData(requestDto);
        return toItemRequestDto(itemRequestRepository.save(toItemRequest(requestDto, requester)));
    }

    @Override
    public Collection<ItemRequestDto> getAllUserRequestsWithResponses(Long userId) {
        validateUserId(userId, userRepository);
        return itemRequestRepository.findAllByRequester_Id(userId).stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemRequestDto> getAllRequestsToResponse(Long userId, Pageable page) {
        validateUserId(userId, userRepository);
        return itemRequestRepository.findAllByAllOtherUsers(userId, page).stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getRequestById(Long userId, Long requestId) {
        validateUserId(userId, userRepository);
        ItemRequest request = validateItemRequestIdAndReturns(requestId, itemRequestRepository);
        return toItemRequestDto(request);
    }
}
