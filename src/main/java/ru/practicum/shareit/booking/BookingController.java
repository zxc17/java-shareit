package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.FindStatus;
import ru.practicum.shareit.exception.ValidationDataException;
import ru.practicum.shareit.marker.Create;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
@Validated
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto add(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                          @Validated({Create.class}) @RequestBody BookingInDto bookingInDto) {
        log.info("Начато выполнение \"Создать бронирование\". " +
                "bookerId={}; RequestBody={}", bookerId, bookingInDto);
        bookingInDto.setBookerId(bookerId);
        bookingInDto.setStatus(BookingStatus.WAITING);
        return bookingService.add(bookingInDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto confirm(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                              @PathVariable Long bookingId,
                              @RequestParam Boolean approved) {
        log.info("Начато выполнение \"Подтверждение/отклонение бронирования\". " +
                "ownerID={}, bookingID={} approved={}", ownerId, bookingId, approved);
        return bookingService.confirm(bookingId, approved, ownerId);
    }

    @GetMapping("/{bookingId}")
    public BookingDto find(@RequestHeader("X-Sharer-User-Id") Long requesterId,
                           @PathVariable Long bookingId) {
        log.info("Начато выполнение \"Найти бронирование\". " +
                "requesterID={}, bookingID={}", requesterId, bookingId);
        return bookingService.find(bookingId, requesterId);
    }

    @GetMapping
    public List<BookingDto> findByUser(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                                       @RequestParam(defaultValue = "ALL", required = false) String state,
                                       @RequestParam(name = "from", defaultValue = "0") Long from,
                                       @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Начато выполнение \"Найти бронирования пользователя\". " +
                "bookerID={}, state={}, from={}, size={}", bookerId, state, from, size);
        if (from < 0 || size <= 0) throw new ValidationDataException(String
                .format("Некорректный запрос. from=%s; size=%s", from, size));
        FindStatus stateEnum;
        try {
            stateEnum = FindStatus.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new ValidationDataException(String.format("Unknown state: %s", state));
        }
        return bookingService.findByUser(stateEnum, bookerId, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> findItemsForUser(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                             @RequestParam(defaultValue = "ALL", required = false) String state,
                                             @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Long from,
                                             @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Начато выполнение \"Найти бронирования вещей пользователя\". " +
                "ownerID={}, state={}, from={}, size={}", ownerId, state, from, size);
        FindStatus stateEnum;
        try {
            stateEnum = FindStatus.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new ValidationDataException(String.format("Unknown state: %s", state));
        }
        return bookingService.findItemsForUser(stateEnum, ownerId, from, size);
    }
}
