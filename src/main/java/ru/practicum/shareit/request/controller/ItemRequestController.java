package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.validator.PageableValidator;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/requests")
@Slf4j
public class ItemRequestController {

    private final ItemRequestService itemRequestService;
    private final PageableValidator pageableValidator;


    @PostMapping
    public ItemRequestDto addNewRequest(@RequestBody ItemRequestDto requestDto, HttpServletRequest request) {
        log.debug("Creating item request element {}", requestDto);
        return itemRequestService.addNewRequest(requestDto, (long) request.getIntHeader("X-Sharer-User-Id"));
    }

    @GetMapping
    public Collection<ItemRequestDto> getAllUserItemsWithResponses(HttpServletRequest request) {
        log.debug("Getting collection of users' items requests");
        return itemRequestService.getAllUserRequestsWithResponses((long) request.getIntHeader("X-Sharer-User-Id"));
    }

    @GetMapping("/all")
    public Collection<ItemRequestDto> getAllCreatedRequests(@RequestParam(defaultValue = "0") Integer from, @RequestParam(defaultValue = "10") Integer size, HttpServletRequest request) {
        pageableValidator.checkingPageableParams(from, size);
        log.debug("Getting collection of created requests");
        Pageable page = PageRequest.of(from, size, Sort.by("creationDate").descending());
        return itemRequestService.getAllRequestsToResponse((long) request.getIntHeader("X-Sharer-User-Id"), page);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@PathVariable Long requestId, HttpServletRequest request) {
        log.debug("Getting request by id: {}", requestId);
        return itemRequestService.getRequestById((long) request.getIntHeader("X-Sharer-User-Id"), requestId);
    }
}
