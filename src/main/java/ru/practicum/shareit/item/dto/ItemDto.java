package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.marker.Create;
import ru.practicum.shareit.marker.Update;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * Общий DTO.
 */
@Getter
@Setter
@ToString
@Builder
public class ItemDto {

    private Long id;

    @NotBlank(groups = {Create.class})
    @Pattern(regexp = ".*\\S.*", groups = {Update.class})  //Хотя бы один не пробельный символ, либо null.
    private String name;

    @NotBlank(groups = {Create.class})
    @Pattern(regexp = ".*\\S.*", groups = {Update.class})
    private String description;

    @NotNull(groups = {Create.class})
    private Boolean available;

    private Long ownerId;   // Приходит в заголовке, а не в теле, поэтому не проверяем.

    private Long requestId; // Не обязательный.
}
