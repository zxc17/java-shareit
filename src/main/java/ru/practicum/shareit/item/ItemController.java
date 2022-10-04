package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
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
import ru.practicum.shareit.marker.Create;
import ru.practicum.shareit.marker.Update;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto add(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                       @Validated({Create.class}) @RequestBody ItemDto itemDto) {
        log.info(String.format("Начато выполнение \"Создать вещь\". " +
                "ownerID=%s, RequestBody=%s", ownerId, itemDto));
        return itemService.add(itemDto, ownerId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                          @PathVariable Long itemId,
                          @Validated({Update.class}) @RequestBody ItemDto itemDto) {
        log.info(String.format("Начато выполнение \"Обновить вещь\". " +
                ", ownerID=%s, itemID=%s, RequestBody=%s", ownerId, itemId, itemDto));
        return itemService.update(itemDto, itemId, ownerId);
    }

    @GetMapping("/{itemId}")
    public ItemViewDto getById(@RequestHeader("X-Sharer-User-Id") Long requesterId,
                               @PathVariable Long itemId) {
        log.info(String.format("Начато выполнение \"Получить вещь по ID\". " +
                "itemID=%s, requesterID=%s", itemId, requesterId));
        return itemService.getById(itemId, requesterId);
    }

    @GetMapping
    public List<ItemViewDto> getListByOwner(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info(String.format("Начато выполнение \"Получить все вещи владельца\". " +
                "ownerID=%s", ownerId));
        return itemService.getListByOwner(ownerId);
    }

    @GetMapping("/search")
    public List<ItemDto> findItems(@RequestParam @NotNull String text) {
        log.info(String.format("Начато выполнение \"Найти вещь\". " +
                "text=%s", text));
        return itemService.findItems(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @PathVariable Long itemId,
                                 @Validated({Create.class}) @RequestBody CommentDto commentDto) {
        log.info(String.format("Начато выполнение \"Добавить комментарий\". " +
                "itemID=%s, userID=%s, RequestBody=%s", itemId, userId, commentDto));
        return itemService.addComment(commentDto, itemId, userId);
    }
}
