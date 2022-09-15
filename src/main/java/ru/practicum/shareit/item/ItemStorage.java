package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.List;

public interface ItemStorage {
    Item add(Item item);

    Item getById(Long itemId);

    List<Item> getListByOwner(User owner);

    Item update(Item item);

    List<Item> findItems(String text);
}
