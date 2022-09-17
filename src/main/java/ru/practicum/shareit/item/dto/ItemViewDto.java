package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO, передающий фронтенду данные для просмотра вещей.
 */
@Getter
@Setter
@Builder
public class ItemViewDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
}
