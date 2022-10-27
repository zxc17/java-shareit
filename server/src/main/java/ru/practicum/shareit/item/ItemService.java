package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemViewDto;

import java.util.List;

public interface ItemService {
    ItemDto add(ItemDto itemDto, Long ownerId);

    ItemViewDto getById(Long itemId, Long requesterId);

    List<ItemViewDto> getListByOwner(Long ownerId, Long from, Integer size);

    ItemDto update(ItemDto itemDto, Long itemId, Long ownerId);

    List<ItemDto> findItems(String text, Long from, Integer size);

    CommentDto addComment(CommentDto commentDto, Long itemId, Long userId);
}
