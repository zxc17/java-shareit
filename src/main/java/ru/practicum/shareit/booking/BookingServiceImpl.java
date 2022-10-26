package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.FindStatus;
import ru.practicum.shareit.exception.ValidationDataException;
import ru.practicum.shareit.exception.ValidationNotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper bookingMapper;

    @Override
    public BookingDto add(BookingInDto bookingInDto) {
        User booker = userRepository.findById(bookingInDto.getBookerId())
                .orElseThrow(() -> new ValidationNotFoundException(String
                        .format("Арендатор ID=%s не найден.", bookingInDto.getBookerId())));
        Item item = itemRepository.findById(bookingInDto.getItemId())
                .orElseThrow(() -> new ValidationNotFoundException(String
                        .format("Вещь ID=%s не найдена.", bookingInDto.getItemId())));
        if (!item.getAvailable())
            throw new ValidationDataException(String
                    .format("Вещь ID=%s недоступна для бронирования.", item.getId()));
        if (booker.getId().equals(item.getOwner().getId()))
            //TODO По тестам ожидается 404 ошибка, на мой взгляд должна быть 403.
            throw new ValidationNotFoundException("Нельзя арендовать собственную вещь.");
        Booking booking = bookingMapper.toBooking(bookingInDto, item, booker);
        checkTimeSlot(booking);
        booking = bookingRepository.save(booking);
        return bookingMapper.toBookingDto(booking);
    }

    @Override
    public BookingDto confirm(Long bookingId, Boolean approved, Long ownerId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ValidationNotFoundException(String
                        .format("Бронь ID=%s не найдена.", bookingId)));
        if (!userRepository.existsById(ownerId))
            throw new ValidationNotFoundException(String
                    .format("Владелец ID=%s не найден.", ownerId));
        if (!booking.getItem().getOwner().getId().equals(ownerId))
            //TODO По тестам ожидается 404 ошибка, на мой взгляд должна быть 403.
            throw new ValidationNotFoundException(String
                    .format("Невозможно обновить. Вещь принадлежит владельцу ID=%s, запрос прислан от ID=%s.",
                            booking.getItem().getOwner().getId(), ownerId));
        if (booking.getStatus() != BookingStatus.WAITING)
            throw new ValidationDataException(String
                    .format("Бронь ID=%s не в статусе WAITING.", bookingId));
        BookingStatus newStatus;
        if (approved) {
            checkTimeSlot(booking);
            newStatus = BookingStatus.APPROVED;
        } else {
            newStatus = BookingStatus.REJECTED;
        }
        booking.setStatus(newStatus);
        booking = bookingRepository.save(booking);
        return bookingMapper.toBookingDto(booking);
    }

    @Override
    public BookingDto find(Long bookingId, Long requesterId) {
        if (!userRepository.existsById(requesterId))
            throw new ValidationNotFoundException(String
                    .format("Пользователь ID=%s не найден.", requesterId));
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ValidationNotFoundException(String
                        .format("Бронь ID=%s не найдена.", bookingId)));
        if (!booking.getItem().getOwner().getId().equals(requesterId) &&
                !booking.getBooker().getId().equals(requesterId))
            //TODO По тестам ожидается 404 ошибка, на мой взгляд должна быть 403.
            throw new ValidationNotFoundException(String
                    .format("Пользователь ID=%s не является ни арендатором, ни владельцем.", requesterId));
        return bookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> findByUser(FindStatus state, Long bookerId, Long from, Integer size) {
        if (!userRepository.existsById(bookerId))
            throw new ValidationNotFoundException(String
                    .format("Пользователь ID=%s не найден.", bookerId));
        List<Booking> bookingList;
        LocalDateTime now = LocalDateTime.now();
        int page = Math.toIntExact(from / size);
        Pageable pageable = PageRequest.of(page, size, Sort.by("start").descending());
        switch (state) {
            case CURRENT:
                bookingList = bookingRepository.findByBooker_IdAndStartBeforeAndEndAfter(bookerId, now, now, pageable);
                break;
            case PAST:
                bookingList = bookingRepository.findByBooker_IdAndEndBefore(bookerId, now, pageable);
                break;
            case FUTURE:
                bookingList = bookingRepository.findByBooker_IdAndStartAfter(bookerId, now, pageable);
                break;
            case WAITING:
                bookingList = bookingRepository.findByBooker_IdAndStatus(bookerId, BookingStatus.WAITING, pageable);
                break;
            case REJECTED:
                bookingList = bookingRepository.findByBooker_IdAndStatus(bookerId, BookingStatus.REJECTED, pageable);
                break;
            case ALL:
                bookingList = bookingRepository.findByBooker_Id(bookerId, pageable);
                break;
            default:
                bookingList = Collections.emptyList();
        }
        return bookingList.stream()
                .map(bookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> findItemsForUser(FindStatus state, Long ownerId, Long from, Integer size) {
        if (!userRepository.existsById(ownerId))
            throw new ValidationNotFoundException(String
                    .format("Пользователь ID=%s не найден.", ownerId));
        List<Booking> bookingList;
        LocalDateTime now = LocalDateTime.now();
        int page = Math.toIntExact(from / size);
        Pageable pageable = PageRequest.of(page, size, Sort.by("start").descending());
        switch (state) {
            case CURRENT:
                bookingList = bookingRepository.findByItem_Owner_IdAndStartBeforeAndEndAfter(ownerId, now, now, pageable);
                break;
            case PAST:
                bookingList = bookingRepository.findByItem_Owner_IdAndEndBefore(ownerId, now, pageable);
                break;
            case FUTURE:
                bookingList = bookingRepository.findByItem_Owner_IdAndStartAfter(ownerId, now, pageable);
                break;
            case WAITING:
                bookingList = bookingRepository.findByItem_Owner_IdAndStatus(ownerId, BookingStatus.WAITING, pageable);
                break;
            case REJECTED:
                bookingList = bookingRepository.findByItem_Owner_IdAndStatus(ownerId, BookingStatus.REJECTED, pageable);
                break;
            case ALL:
                bookingList = bookingRepository.findByItem_Owner_Id(ownerId, pageable);
                break;
            default:
                bookingList = Collections.emptyList();
        }
        return bookingList.stream()
                .map(bookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    private void checkTimeSlot(Booking booking) {
        if (booking.getEnd().isBefore(booking.getStart()))
            throw new ValidationDataException("Выбранное время старта бронирования позже окончания.");
        if (booking.getStart().isBefore(LocalDateTime.now()))
            throw new ValidationDataException("Выбранное время старта бронирования в прошлом.");
        if (booking.getEnd().isBefore(LocalDateTime.now()))
            throw new ValidationDataException("Выбранное время окончания бронирования в прошлом.");
        if (bookingRepository.findBusyTimeSlot(
                        booking.getId(), booking.getStart(), booking.getEnd(), booking.getItem().getId())
                .stream().anyMatch(b -> b.getStatus() == BookingStatus.APPROVED))
            throw new ValidationDataException("Выбранное время бронирования уже занято.");
    }
}
