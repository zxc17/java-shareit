package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;
    private final String headerId = "X-Sharer-User-Id";

    @PostMapping
    public ItemRequestDto add(@RequestHeader(headerId) Long requesterId,
                              @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Сервер принял запрос \"Создать запрос\". " +
                "requesterId={}; RequestBody={}", requesterId, itemRequestDto);
        return itemRequestService.add(requesterId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestDto> getByRequester(@RequestHeader(headerId) Long requesterId) {
        log.info("Сервер принял запрос \"Получить запросы пользователя\". " +
                "requesterId={}", requesterId);
        return itemRequestService.getByRequester(requesterId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getMadeByOther(
            @RequestHeader(headerId) Long requesterId,
            @RequestParam(name = "from", defaultValue = "0") Long from,
            @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Сервер принял запрос \"Получить запросы других пользователей\". " +
                "requesterId={}; from={}; size={}", requesterId, from, size);
        return itemRequestService.getMadeByOther(requesterId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getById(@RequestHeader(headerId) Long requesterId,
                                  @PathVariable Long requestId) {
        log.info("Сервер принял запрос \"Получить запрос по ID\". " +
                "requestId={}", requestId);
        return itemRequestService.getById(requesterId, requestId);
    }
}
