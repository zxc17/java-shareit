package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ValidationNotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRequestMapper itemRequestMapper;

    @Override
    public ItemRequestDto add(Long requesterId, ItemRequestDto itemRequestDto) {
        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new ValidationNotFoundException(String
                        .format("Пользователь userID=%s не найден.", requesterId)));
        itemRequestDto.setRequester(requester);
        itemRequestDto.setCreated(LocalDateTime.now());
        ItemRequest itemRequest = itemRequestMapper.toItemRequest(itemRequestDto);
        itemRequest = itemRequestRepository.save(itemRequest);
        return itemRequestMapper.toItemRequestDto(itemRequest, false);
    }

    @Override
    public List<ItemRequestDto> getByRequester(Long requesterId) {
        if (!userRepository.existsById(requesterId))
            throw new ValidationNotFoundException(String.format("Пользователь userID=%s не найден.", requesterId));
        return itemRequestRepository.findByRequester_Id(requesterId)
                .stream()
                .map(itemRequest -> itemRequestMapper.toItemRequestDto(itemRequest, true))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getMadeByOther(Long requesterId, Long from, Integer size) {
        if (!userRepository.existsById(requesterId))
            throw new ValidationNotFoundException(String.format("Пользователь userID=%s не найден.", requesterId));
        int page = Math.toIntExact(from / size);
        Pageable pageable = PageRequest.of(page, size);
        return itemRequestRepository.findByRequester_IdNot(requesterId, pageable).stream()
                .map(itemRequest -> itemRequestMapper.toItemRequestDto(itemRequest, true))
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getById(Long requesterId, Long requestId) {
        if (!userRepository.existsById(requesterId))
            throw new ValidationNotFoundException(String.format("Пользователь userID=%s не найден.", requesterId));
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new ValidationNotFoundException(String
                        .format("Запрос requestId=%s не найден.", requestId)));
        return itemRequestMapper.toItemRequestDto(itemRequest, true);
    }
}
