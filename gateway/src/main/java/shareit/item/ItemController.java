package shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import shareit.validator.PageableValidator;
import shareit.validator.ItemValidator;

import javax.validation.constraints.Positive;


@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private final PageableValidator pageableValidator;
    private final ItemValidator itemValidator;
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") @Positive long userId) {
        itemValidator.validateItemData(itemDto);
        log.debug("Gateway: Creating item element {}", itemDto);
        return itemClient.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@PathVariable @Positive long itemId, @RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") @Positive long userId) {
        itemValidator.validateItemDataUpdate(itemDto);
        log.debug("Gateway: Updating item element by id {}", itemId);
        itemDto.setId(itemId);
        return itemClient.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@PathVariable @Positive long itemId, @RequestHeader("X-Sharer-User-Id") @Positive long userId) {
        log.debug("Gateway: Getting item by id : {}", itemId);
        return itemClient.getItemById(userId, itemId);
    }

    @GetMapping()
    public ResponseEntity<Object> getUserItems(@RequestParam(defaultValue = "0") Integer from, @RequestParam(defaultValue = "10") Integer size,
                                               @RequestHeader("X-Sharer-User-Id") @Positive long userId) {
        pageableValidator.checkingPageableParams(from, size);
        log.debug("Gateway: Getting all items by userId {}", userId);
        return itemClient.getUserItems(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getItemsBySearch(@RequestParam(defaultValue = "0") Integer from, @RequestParam(defaultValue = "10") Integer size,
                                                   @RequestParam String text, @RequestHeader("X-Sharer-User-Id") @Positive long userId) {
        pageableValidator.checkingPageableParams(from, size);
        log.debug("Gateway: Getting items by search text: {}", text);
        return itemClient.getItemsBySearch(userId, from, size, text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createCommentToItem(@PathVariable @Positive Long itemId, @RequestBody CommentDto comment,
                                                      @RequestHeader("X-Sharer-User-Id") long userId) {
        itemValidator.validateCommentData(comment);
        log.debug("Gateway: Creating comment to item by userId {}", userId);
        return itemClient.createCommentToItem(userId, itemId, comment);
    }
}
