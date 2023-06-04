package ru.practicum.shareit.request.service;


import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.Collection;


public interface ItemRequestService {

    ItemRequestDto addNewRequest(ItemRequestDto requestDto, Long userId);

    Collection<ItemRequestDto> getAllUserRequestsWithResponses(Long userId);

    Collection<ItemRequestDto> getAllRequestsToResponse(Long userId, Pageable page);

    ItemRequestDto getRequestById(Long userId, Long requestId);

}
