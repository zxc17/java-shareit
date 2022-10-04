package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.marker.Create;
import ru.practicum.shareit.marker.Update;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;

/**
 * Общий DTO.
 */
@Getter
@Setter
@ToString
@Builder
public class CommentDto {

    private Long id;

    @NotBlank(groups = {Create.class})
    @Pattern(regexp = ".*\\S.*", groups = {Update.class})  //Хотя бы один не пробельный символ, либо null.
    private String text;

    private Long itemId;    // Приходит в переменной пути.

    private Long authorId;  // Приходит в заголовке.

    private String authorName;

    private LocalDateTime created;
}
