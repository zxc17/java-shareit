package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.marker.Create;
import ru.practicum.shareit.marker.Update;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@ToString
@Builder
public class UserDto {
    private Long id;
    @NotBlank(groups = {Create.class})
    @Pattern(regexp = "^\\S+$", groups = {Update.class})    // Запрет пробельных символов и пустых строк, разрешен null.
    private String name;
    @NotNull(groups = {Create.class})
    @Email(groups = {Create.class, Update.class})
    @Pattern(regexp = "^\\S+$", groups = {Create.class, Update.class})
    private String email;
}
