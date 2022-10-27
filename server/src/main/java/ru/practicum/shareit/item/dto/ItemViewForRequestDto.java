package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * DTO, передающий фронтенду данные для просмотра вещей.
 */
@Getter
@Setter
@Builder
public class ItemViewForRequestDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private List<CommentDto> comments;
    private Long requestId;
}
