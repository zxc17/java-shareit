package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RequestStorageImpl implements RequestStorage {


    @Override
    public ItemRequest getById(Long requestId) {
        return null;
    }
}
