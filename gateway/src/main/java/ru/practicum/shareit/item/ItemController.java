package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.marker.Create;
import ru.practicum.shareit.marker.Update;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/items")
@Validated
public class ItemController {
    private final ItemClient itemClient;
    private final String headerId = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> add(@RequestHeader(headerId) Long ownerId,
                                      @Validated({Create.class}) @RequestBody ItemDto itemDto) {
        log.info("Начато выполнение \"Создать вещь\". " +
                "ownerID={}, RequestBody={}", ownerId, itemDto);
        return itemClient.add(itemDto, ownerId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestHeader(headerId) Long ownerId,
                                         @PathVariable Long itemId,
                                         @Validated({Update.class}) @RequestBody ItemDto itemDto) {
        log.info("Начато выполнение \"Обновить вещь\". " +
                ", ownerID={}, itemID={}, RequestBody={}", ownerId, itemId, itemDto);
        return itemClient.update(itemDto, itemId, ownerId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getById(@RequestHeader(headerId) Long requesterId,
                                          @PathVariable Long itemId) {
        log.info("Начато выполнение \"Получить вещь по ID\". " +
                "itemID={}, requesterID={}", itemId, requesterId);
        return itemClient.getById(itemId, requesterId);
    }

    @GetMapping
    public ResponseEntity<Object> getListByOwner(
            @RequestHeader(headerId) Long ownerId,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Long from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size
    ) {
        log.info("Начато выполнение \"Получить все вещи владельца\". " +
                "ownerID={}, from={}, size={}", ownerId, from, size);
        return itemClient.getListByOwner(ownerId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> findItems(@RequestHeader(headerId) Long requesterId,
                                            @RequestParam @NotNull String text,
                                            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Long from,
                                            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Начато выполнение \"Найти вещь\". " +
                "text={}, from={}, size={}", text, from, size);
        return itemClient.findItems(requesterId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(headerId) Long userId,
                                             @PathVariable Long itemId,
                                             @Validated({Create.class}) @RequestBody CommentDto commentDto) {
        log.info("Начато выполнение \"Добавить комментарий\". " +
                "itemID={}, userID={}, RequestBody={}", itemId, userId, commentDto);
        return itemClient.addComment(commentDto, itemId, userId);
    }
}
