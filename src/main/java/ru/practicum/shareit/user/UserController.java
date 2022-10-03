package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.marker.Create;
import ru.practicum.shareit.marker.Update;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

/**
 * UserController.
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto add(@Validated({Create.class}) @RequestBody UserDto userDto) {
        log.info("Начато выполнение \"Создать пользователя\".");
        return userService.add(userDto);
    }

    @GetMapping("/{id}")
    public UserDto getById(@PathVariable Long id) {
        log.info("Начато выполнение \"Получить пользователя по ID\".");
        return userService.getById(id);
    }

    @GetMapping
    public List<UserDto> getAll() {
        log.info("Начато выполнение \"Получить список всех пользователей\".");
        return userService.getAll();
    }

    @PatchMapping("/{id}")
    public UserDto update(@PathVariable Long id,
                          @Validated({Update.class}) @RequestBody UserDto userDto) {
        log.info("Начато выполнение \"Обновить данные пользователя по ID\".");
        return userService.update(id, userDto);
    }

    @DeleteMapping("/{id}")
    public void remove(@PathVariable Long id) {
        log.info("Начато выполнение \"Удалить пользователя по ID\".");
        userService.remove(id);
    }
}
