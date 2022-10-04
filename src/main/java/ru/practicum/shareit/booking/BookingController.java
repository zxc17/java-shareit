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

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto add(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                          @Validated({Create.class}) @RequestBody BookingInDto bookingInDto) {
        log.info(String.format("Начато выполнение \"Создать бронирование\". " +
                        "bookerId=%s; RequestBody=%s", bookerId, bookingInDto));
        bookingInDto.setBookerId(bookerId);
        bookingInDto.setStatus(BookingStatus.WAITING);
        return bookingService.add(bookingInDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto confirm(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                              @PathVariable Long bookingId,
                              @RequestParam Boolean approved) {
        log.info(String.format("Начато выполнение \"Подтверждение/отклонение бронирования\". " +
                "ownerID=%s, bookingID=%s approved=%s", ownerId, bookingId, approved));
        return bookingService.confirm(bookingId, approved, ownerId);
    }

    @GetMapping("/{bookingId}")
    public BookingDto find(@RequestHeader("X-Sharer-User-Id") Long requesterId,
                           @PathVariable Long bookingId) {
        log.info(String.format("Начато выполнение \"Найти бронирование\". " +
                "requesterID=%s, bookingID=%s", requesterId, bookingId));
        return bookingService.find(bookingId, requesterId);
    }

    @GetMapping
    public List<BookingDto> findByUser(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                                       @RequestParam(defaultValue = "ALL", required = false) String state) {
        log.info(String.format("Начато выполнение \"Найти бронирования пользователя\". " +
                "bookerID=%s, state=%s", bookerId, state));
        FindStatus stateEnum;
        try {
            stateEnum = FindStatus.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new ValidationDataException(String.format("Unknown state: %s", state));
        }
        return bookingService.findByUser(stateEnum, bookerId);
    }

    @GetMapping("/owner")
    public List<BookingDto> findItemsForUser(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                             @RequestParam(defaultValue = "ALL", required = false) String state) {
        log.info(String.format("Начато выполнение \"Найти бронирования вещей пользователя\". " +
                "ownerID=%s, state=%s", ownerId, state));
        FindStatus stateEnum;
        try {
            stateEnum = FindStatus.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new ValidationDataException(String.format("Unknown state: %s", state));
        }
        return bookingService.findItemsForUser(stateEnum, ownerId);
    }
}
