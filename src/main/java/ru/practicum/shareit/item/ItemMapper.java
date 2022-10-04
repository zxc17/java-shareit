package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingViewDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemViewDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ItemMapper {
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final BookingMapper bookingMapper;
    private final CommentMapper commentMapper;

    public ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .ownerId(item.getOwner().getId())
                .requestId(item.getRequest() != null ? item.getRequest().getId() : null)
                .build();
    }

    public Item toItem(ItemDto itemDto, User owner, ItemRequest request) {
        // Проверки на null в сервисе.
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(owner)
                .request(request)
                .build();
    }

    /**
     * @param item             Вещь, которую нужно просмотреть.
     * @param isAddBookingDate Флаг, нужно ли добавлять даты бронирования.
     * @return DTO для просмотра вещи.
     */
    public ItemViewDto toItemViewDto(Item item, boolean isAddBookingDate) {
        BookingViewDto lastBooking;
        BookingViewDto nextBooking;
        List<CommentDto> comments;

        if (isAddBookingDate) {
            List<Booking> bookingList = bookingRepository.findByItem_Id(item.getId());
            LocalDateTime now = LocalDateTime.now();
            lastBooking = bookingList.stream()
                    .filter(b -> b.getStart().isBefore(now))
                    .max(Comparator.comparing((Booking::getStart)))
                    .map(bookingMapper::toBookingViewDto).orElse(null);
            nextBooking = bookingList.stream()
                    .filter(b -> b.getStart().isAfter(now))
                    .min(Comparator.comparing((Booking::getStart)))
                    .map(bookingMapper::toBookingViewDto).orElse(null);
        } else {
            lastBooking = null;
            nextBooking = null;
        }

        comments = commentRepository.findByItemId(item.getId())
                .stream()
                .map(commentMapper::toCommentDto)
                .collect(Collectors.toList());

        return ItemViewDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .comments(comments)
                .build();
    }
}
