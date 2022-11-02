package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.dto.BookingViewDto;

import java.util.List;

/**
 * DTO, передающий фронтенду данные для просмотра вещей.
 */
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class ItemViewDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingViewDto lastBooking;
    private BookingViewDto nextBooking;
    private List<CommentDto> comments;
}
