package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ValidationForbiddenException;
import ru.practicum.shareit.exception.ValidationNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemViewDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.RequestStorage;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserStorage;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;
    private final RequestStorage requestStorage;

    @Override
    public ItemDto add(ItemDto itemDto, Long ownerId) {
        User owner = userStorage.getById(ownerId);
        if (owner == null) throw new ValidationNotFoundException(String
                .format("Владелец ID=%s не найден.", ownerId));
        //TODO В следующих спринтах понадобится добавить запись в requestStorage.
        ItemRequest request = itemDto.getRequestId() != null ? requestStorage.getById(itemDto.getRequestId()) : null;
        Item item = ItemMapper.toItem(itemDto, owner, request);
        item = itemStorage.add(item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto getById(Long itemId) {
        Item item = itemStorage.getById(itemId);
        if (item == null) throw new ValidationNotFoundException(String
                .format("Вещь ID=%s не найдена.", itemId));
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemViewDto> getListByOwner(Long ownerId) {
        User owner = userStorage.getById(ownerId);
        if (owner == null) throw new ValidationNotFoundException(String
                .format("Владелец ID=%s не найден.", ownerId));
        List<Item> itemsByOwner = itemStorage.getListByOwner(owner);
        return itemsByOwner.stream()
                .map(ItemMapper::toItemViewDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto update(ItemDto itemDto, Long itemId, Long ownerId) {
        User owner = userStorage.getById(ownerId);
        if (owner == null) throw new ValidationNotFoundException(String
                .format("Владелец ID=%s не найден.", ownerId));
        Item item = itemStorage.getById(itemId);
        if (item == null) throw new ValidationNotFoundException(String
                .format("Вещь ID=%s не найдена.", itemId));
        if (!item.getOwner().getId().equals(ownerId)) throw new ValidationForbiddenException(String
                .format("Невозможно обновить. Вещь принадлежит владельцу ID=%s, запрос прислан от ID=%s.",
                        item.getOwner().getId(), ownerId));
        if (itemDto.getName() != null) item.setName(itemDto.getName());
        if (itemDto.getDescription() != null) item.setDescription(itemDto.getDescription());
        if (itemDto.getAvailable() != null) item.setAvailable(itemDto.getAvailable());
        //TODO Владельца обновлять не нужно, а с полем request неясно... Будет уточнено в следующих спринтах.
        item = itemStorage.update(item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> findItems(String text) {
        if (text.isEmpty()) return Collections.emptyList();
        List<Item> items = itemStorage.findItems(text);
        return items.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
