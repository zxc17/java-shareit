package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.FindStatus;
import ru.practicum.shareit.exception.ValidationDataException;
import ru.practicum.shareit.marker.Create;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.util.Constants.HEADER_ID;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> add(@RequestHeader(HEADER_ID) Long bookerId,
                                      @Validated({Create.class}) @RequestBody BookingInDto bookingInDto) {
        log.info("Начато выполнение \"Создать бронирование\". " +
                "bookerId={}; RequestBody={}", bookerId, bookingInDto);
        bookingInDto.setBookerId(bookerId);
        bookingInDto.setStatus(BookingStatus.WAITING);
        return bookingClient.add(bookerId, bookingInDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> confirm(@RequestHeader(HEADER_ID) Long ownerId,
                                          @PathVariable Long bookingId,
                                          @RequestParam Boolean approved) {
        log.info("Начато выполнение \"Подтверждение/отклонение бронирования\". " +
                "ownerID={}, bookingID={} approved={}", ownerId, bookingId, approved);
        return bookingClient.confirm(bookingId, approved, ownerId);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> find(@RequestHeader(HEADER_ID) Long requesterId,
                                       @PathVariable Long bookingId) {
        log.info("Начато выполнение \"Найти бронирование\". " +
                "requesterID={}, bookingID={}", requesterId, bookingId);
        return bookingClient.find(bookingId, requesterId);
    }

    @GetMapping
    public ResponseEntity<Object> findByUser(@RequestHeader(HEADER_ID) Long bookerId,
                                             @RequestParam(defaultValue = "ALL", required = false) String state,
                                             @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Long from,
                                             @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Начато выполнение \"Найти бронирования пользователя\". " +
                "bookerID={}, state={}, from={}, size={}", bookerId, state, from, size);
        try {
            FindStatus.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new ValidationDataException(String.format("Unknown state: %s", state));
        }
        return bookingClient.findByUser(state, bookerId, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> findItemsForUser(
            @RequestHeader(HEADER_ID) Long ownerId,
            @RequestParam(defaultValue = "ALL", required = false) String state,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Long from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Начато выполнение \"Найти бронирования вещей пользователя\". " +
                "ownerID={}, state={}, from={}, size={}", ownerId, state, from, size);
        try {
            FindStatus.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new ValidationDataException(String.format("Unknown state: %s", state));
        }
        return bookingClient.findItemsForUser(state, ownerId, from, size);
    }
}
