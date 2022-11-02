package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ItemRequestMapper {
    private final ItemMapper itemMapper;
    private final ItemRepository itemRepository;

    // Флаг isFindItems показывает, нужно ли искать ответы на данный запрос.
    // Чтобы не дергать базу, когда это не требуется.
    public ItemRequestDto toItemRequestDto(ItemRequest itemRequest, boolean isFindItems) {
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .requester(itemRequest.getRequester())
                .created(itemRequest.getCreated())
                .build();
        if (isFindItems)
            itemRequestDto.setItems(
                    itemRepository.findByRequest_Id(itemRequest.getRequester().getId()).stream()
                    .map(itemMapper::toItemViewForRequestDto)
                    .collect(Collectors.toList()));
        return itemRequestDto;
    }

    public ItemRequest toItemRequest(ItemRequestDto itemRequestDto) {
        return ItemRequest.builder()
                .id(itemRequestDto.getId())
                .description(itemRequestDto.getDescription())
                .requester(itemRequestDto.getRequester())
                .created(itemRequestDto.getCreated())
                .build();
    }
}
