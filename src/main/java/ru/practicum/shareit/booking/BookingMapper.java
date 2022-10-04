package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.dto.BookingViewDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

@Component
@RequiredArgsConstructor
public class BookingMapper {

    public Booking toBooking(BookingInDto bookingInDto, Item item, User booker) {
        return Booking.builder()
                .id(bookingInDto.getId())
                .start(bookingInDto.getStart())
                .end(bookingInDto.getEnd())
                .item(item)
                .booker(booker)
                .status(bookingInDto.getStatus())
                .build();
    }

    public BookingDto toBookingDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(booking.getItem())
                .booker(booking.getBooker())
                .status(booking.getStatus())
                .build();
    }

    public BookingViewDto toBookingViewDto(Booking booking) {
        if (booking == null)
            return null;
        else
            return BookingViewDto.builder()
                    .id(booking.getId())
                    .bookerId(booking.getBooker().getId())
                    .build();
    }
}
