package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.marker.Create;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 *
 */
@Getter
@Setter
@Builder
public class BookingInDto {

    private Long id;

    @NotNull(groups = {Create.class})
    private LocalDateTime start;

    @NotNull(groups = {Create.class})
    private LocalDateTime end;

    @NotNull(groups = {Create.class})
    private Long itemId;

    private Long bookerId;

    private BookingStatus status;

}
