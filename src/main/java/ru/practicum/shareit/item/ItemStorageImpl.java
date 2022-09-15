package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ItemStorageImpl implements ItemStorage {
    private final Map<Long, Item> items;
    private long id = 0;

    @Override
    public Item add(Item item) {
        long newId = getNewId();
        item.setId(newId);
        items.put(newId, item);
        return item;
    }

    @Override
    public Item getById(Long itemId) {
        return items.get(itemId);
    }

    @Override
    public List<Item> getListByOwner(User owner) {
        return items.values().stream()
                .filter(i -> i.getOwner().equals(owner))
                .collect(Collectors.toList());
    }

    @Override
    public Item update(Item item) {
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public List<Item> findItems(String text) {
        if (text.isEmpty()) return Collections.emptyList();
        String t = text.toLowerCase();
        return items.values().stream()
                .filter(i -> i.getAvailable())
                .filter(i -> i.getDescription().toLowerCase().contains(t) || i.getName().toLowerCase().contains(t))
                .collect(Collectors.toList());
    }

    private long getNewId() {
        while (items.containsKey(++id)) ;
        return id;
    }
}
