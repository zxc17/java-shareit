package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemViewDto;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;
    private final String headerId = "X-Sharer-User-Id";

    @PostMapping
    public ItemDto add(@RequestHeader(headerId) Long ownerId,
                       @RequestBody ItemDto itemDto) {
        log.info("Сервер принял запрос \"Создать вещь\". " +
                "ownerID={}, RequestBody={}", ownerId, itemDto);
        return itemService.add(itemDto, ownerId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader(headerId) Long ownerId,
                          @PathVariable Long itemId,
                          @RequestBody ItemDto itemDto) {
        log.info("Сервер принял запрос \"Обновить вещь\". " +
                ", ownerID={}, itemID={}, RequestBody={}", ownerId, itemId, itemDto);
        return itemService.update(itemDto, itemId, ownerId);
    }

    @GetMapping("/{itemId}")
    public ItemViewDto getById(@RequestHeader(headerId) Long requesterId,
                               @PathVariable Long itemId) {
        log.info("Сервер принял запрос \"Получить вещь по ID\". " +
                "itemID={}, requesterID={}", itemId, requesterId);
        return itemService.getById(itemId, requesterId);
    }

    @GetMapping
    public List<ItemViewDto> getListByOwner(@RequestHeader(headerId) Long ownerId,
                                            @RequestParam(name = "from", defaultValue = "0") Long from,
                                            @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Сервер принял запрос \"Получить все вещи владельца\". " +
                "ownerID={}, from={}, size={}", ownerId, from, size);
        return itemService.getListByOwner(ownerId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> findItems(@RequestParam String text,
                                   @RequestParam(name = "from", defaultValue = "0") Long from,
                                   @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Сервер принял запрос \"Найти вещь\". " +
                "text={}, from={}, size={}", text, from, size);
        return itemService.findItems(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(headerId) Long userId,
                                 @PathVariable Long itemId,
                                 @RequestBody CommentDto commentDto) {
        log.info("Сервер принял запрос \"Добавить комментарий\". " +
                "itemID={}, userID={}, RequestBody={}", itemId, userId, commentDto);
        return itemService.addComment(commentDto, itemId, userId);
    }
}
