package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.marker.Create;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.util.Constants.HEADER_ID;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> add(@RequestHeader(HEADER_ID) Long requesterId,
                                      @Validated({Create.class}) @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Начато выполнение \"Создать запрос\". " +
                "requesterId={}; RequestBody={}", requesterId, itemRequestDto);
        return itemRequestClient.add(requesterId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getByRequester(@RequestHeader(HEADER_ID) Long requesterId) {
        log.info("Начато выполнение \"Получить запросы пользователя\". " +
                "requesterId={}", requesterId);
        return itemRequestClient.getByRequester(requesterId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getMadeByOther(
            @RequestHeader(HEADER_ID) Long requesterId,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Long from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Начато выполнение \"Получить запросы других пользователей\". " +
                "requesterId={}; from={}; size={}", requesterId, from, size);
        return itemRequestClient.getMadeByOther(requesterId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getById(@RequestHeader(HEADER_ID) Long requesterId,
                                  @PathVariable Long requestId) {
        log.info("Начато выполнение \"Получить запрос по ID\". " +
                "requestId={}", requestId);
        return itemRequestClient.getById(requesterId, requestId);
    }
}
