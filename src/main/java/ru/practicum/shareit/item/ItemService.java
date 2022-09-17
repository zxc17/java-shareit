package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemViewDto;

import java.util.List;

public interface ItemService {
    ItemDto add(ItemDto itemDto, Long ownerId);

    ItemDto getById(Long itemId);

    List<ItemViewDto> getListByOwner(Long ownerId);

    ItemDto update(ItemDto itemDto, Long itemId, Long ownerId);

    List<ItemDto> findItems(String text);
}
