package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.model.FindStatus;

import java.util.List;

import static ru.practicum.shareit.util.Constants.HEADER_ID;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto add(@RequestHeader(HEADER_ID) Long bookerId,
                          @RequestBody BookingInDto bookingInDto) {
        log.info("Сервер принял запрос \"Создать бронирование\". " +
                "bookerId={}; RequestBody={}", bookerId, bookingInDto);
        return bookingService.add(bookingInDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto confirm(@RequestHeader(HEADER_ID) Long ownerId,
                              @PathVariable Long bookingId,
                              @RequestParam Boolean approved) {
        log.info("Сервер принял запрос \"Подтверждение/отклонение бронирования\". " +
                "ownerID={}, bookingID={} approved={}", ownerId, bookingId, approved);
        return bookingService.confirm(bookingId, approved, ownerId);
    }

    @GetMapping("/{bookingId}")
    public BookingDto find(@RequestHeader(HEADER_ID) Long requesterId,
                           @PathVariable Long bookingId) {
        log.info("Сервер принял запрос \"Найти бронирование\". " +
                "requesterID={}, bookingID={}", requesterId, bookingId);
        return bookingService.find(bookingId, requesterId);
    }

    @GetMapping
    public List<BookingDto> findByUser(@RequestHeader(HEADER_ID) Long bookerId,
                                       @RequestParam(defaultValue = "ALL", required = false) String state,
                                       @RequestParam(name = "from", defaultValue = "0") Long from,
                                       @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Сервер принял запрос \"Найти бронирования пользователя\". " +
                "bookerID={}, state={}, from={}, size={}", bookerId, state, from, size);
        FindStatus stateEnum;
        stateEnum = FindStatus.valueOf(state);
        return bookingService.findByUser(stateEnum, bookerId, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> findItemsForUser(@RequestHeader(HEADER_ID) Long ownerId,
                                             @RequestParam(defaultValue = "ALL", required = false) String state,
                                             @RequestParam(name = "from", defaultValue = "0") Long from,
                                             @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Сервер принял запрос \"Найти бронирования вещей пользователя\". " +
                "ownerID={}, state={}, from={}, size={}", ownerId, state, from, size);
        FindStatus stateEnum;
        stateEnum = FindStatus.valueOf(state);
        return bookingService.findItemsForUser(stateEnum, ownerId, from, size);
    }
}
