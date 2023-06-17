package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.validator.ItemRequestValidator;
import ru.practicum.shareit.validator.UserValidator;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.stream.Collectors;

import static ru.practicum.shareit.request.dto.mapper.ItemRequestMapper.*;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserValidator userValidator;
    private final ItemRequestValidator itemRequestValidator;

    @Override
    public ItemRequestDto addNewRequest(ItemRequestDto requestDto, Long userId) {
        User requester = userValidator.validateUserIdAndReturn(userId);
        requestDto.setCreated(LocalDateTime.now());
        itemRequestValidator.validateItemRequestData(requestDto);
        return toItemRequestDto(itemRequestRepository.save(toItemRequest(requestDto, requester)));
    }

    @Override
    public Collection<ItemRequestDto> getAllUserRequestsWithResponses(Long userId) {
        userValidator.validateUserId(userId);
        return itemRequestRepository.findAllByRequester_Id(userId).stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemRequestDto> getAllRequestsToResponse(Long userId, Pageable page) {
        userValidator.validateUserId(userId);
        return itemRequestRepository.findAllByAllOtherUsers(userId, page).stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getRequestById(Long userId, Long requestId) {
        userValidator.validateUserId(userId);
        ItemRequest request = itemRequestValidator.validateItemRequestIdAndReturns(requestId);
        return toItemRequestDto(request);
    }
}
