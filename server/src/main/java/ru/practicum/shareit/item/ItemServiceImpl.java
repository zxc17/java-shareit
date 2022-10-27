package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.ValidationDataException;
import ru.practicum.shareit.exception.ValidationForbiddenException;
import ru.practicum.shareit.exception.ValidationNotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemViewDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemRequestRepository requestRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;

    @Override
    public ItemDto add(ItemDto itemDto, Long ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new ValidationNotFoundException(String
                        .format("Владелец ID=%s не найден.", ownerId)));
        ItemRequest request = itemDto.getRequestId() != null ?
                requestRepository.findById(itemDto.getRequestId()).orElse(null) : null;
        Item item = itemMapper.toItem(itemDto, owner, request);
        item = itemRepository.save(item);
        return itemMapper.toItemDto(item);
    }

    @Override
    public ItemViewDto getById(Long itemId, Long requesterId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ValidationNotFoundException(String
                        .format("Вещь ID=%s не найдена.", itemId)));
        // Если запрос от владельца вещи, то нужно добавить инфу о последнем и ближайшем бронированиях.
        boolean isAddBookingDate = item.getOwner().getId().equals(requesterId);
        return itemMapper.toItemViewDto(item, isAddBookingDate);
    }

    @Override
    public List<ItemViewDto> getListByOwner(Long ownerId, Long from, Integer size) {
        if (!userRepository.existsById(ownerId)) throw new ValidationNotFoundException(String
                .format("Владелец ID=%s не найден.", ownerId));
        int page = Math.toIntExact(from / size);
        Pageable pageable = PageRequest.of(page, size);
        List<Item> itemsByOwner = itemRepository.findByOwner_Id(ownerId, pageable);
        return itemsByOwner.stream()
                .sorted(Comparator.comparing(Item::getId))
                .map(item -> itemMapper.toItemViewDto(item, true))
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto update(ItemDto itemDto, Long itemId, Long ownerId) {
        if (!userRepository.existsById(ownerId))
            throw new ValidationNotFoundException(String
                    .format("Владелец ID=%s не найден.", ownerId));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ValidationNotFoundException(String
                        .format("Вещь ID=%s не найдена.", itemId)));
        if (!item.getOwner().getId().equals(ownerId)) throw new ValidationForbiddenException(String
                .format("Невозможно обновить. Вещь принадлежит владельцу ID=%s, запрос прислан от ID=%s.",
                        item.getOwner().getId(), ownerId));
        if (itemDto.getName() != null) item.setName(itemDto.getName());
        if (itemDto.getDescription() != null) item.setDescription(itemDto.getDescription());
        if (itemDto.getAvailable() != null) item.setAvailable(itemDto.getAvailable());
        item = itemRepository.save(item);
        return itemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> findItems(String text, Long from, Integer size) {
        if (text.isEmpty()) return Collections.emptyList();
        int page = Math.toIntExact(from / size);
        Pageable pageable = PageRequest.of(page, size);
        List<Item> items = itemRepository.findByNameOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(
                text, text, pageable);
        return items.stream()
                .filter(Item::getAvailable)
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto addComment(CommentDto commentDto, Long itemId, Long userId) {
        if (!userRepository.existsById(userId))
            throw new ValidationNotFoundException(String
                    .format("Пользователь ID=%s не найден.", userId));
        if (!itemRepository.existsById(itemId))
            throw new ValidationNotFoundException(String
                    .format("Вещь ID=%s не найдена.", itemId));
        if (bookingRepository.countingUsages(itemId, userId, BookingStatus.APPROVED, LocalDateTime.now()).size() == 0)
            throw new ValidationDataException(String
                    .format("Пользователь ID=%s не пользовался вещью ID=%s.", userId, itemId));
        commentDto.setItemId(itemId);
        commentDto.setAuthorId(userId);
        commentDto.setCreated(LocalDateTime.now());
        Comment comment = commentMapper.toComment(commentDto);
        commentRepository.save(comment);
        return commentMapper.toCommentDto(comment);
    }
}
