package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.item.dto.ItemViewForRequestDto;
import ru.practicum.shareit.marker.Create;
import ru.practicum.shareit.marker.Update;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@Builder
public class ItemRequestDto {

    private Long id;

    @NotBlank(groups = {Create.class})
    @Pattern(regexp = "^\\S+$", groups = {Update.class})    // Запрет пробельных символов и пустых строк, разрешен null.
    private String description;

    private User requester; // На входе отсутствует.

    private LocalDateTime created; // На входе отсутствует.

    private List<ItemViewForRequestDto> items; // На входе отсутствует.
}
