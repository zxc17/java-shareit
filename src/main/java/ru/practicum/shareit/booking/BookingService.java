package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.model.FindStatus;

import java.util.List;

public interface BookingService {
    BookingDto add(BookingInDto bookingInDto);

    BookingDto confirm(Long bookingId, Boolean approved, Long ownerId);

    BookingDto find(Long bookingId, Long ownerId);

    List<BookingDto> findByUser(FindStatus state, Long bookerId);

    List<BookingDto> findItemsForUser(FindStatus state, Long ownerId);
}
