package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto add(Long requesterId, ItemRequestDto itemRequestDto);

    List<ItemRequestDto> getByRequester(Long requesterId);

    List<ItemRequestDto> getMadeByOther(Long requesterId, Long from, Integer size);

    ItemRequestDto getById(Long requesterId, Long requestId);
}
