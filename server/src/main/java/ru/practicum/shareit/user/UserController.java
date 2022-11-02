package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto add(@RequestBody UserDto userDto) {
        log.info("Сервер принял запрос \"Создать пользователя\". " +
                "RequestBody={}", userDto);
        return userService.add(userDto);
    }

    @GetMapping("/{id}")
    public UserDto getById(@PathVariable Long id) {
        log.info("Сервер принял запрос \"Получить пользователя по ID\". " +
                "userID={}", id);
        return userService.getById(id);
    }

    @GetMapping
    public List<UserDto> getAll() {
        log.info("Сервер принял запрос \"Получить список всех пользователей\".");
        return userService.getAll();
    }

    @PatchMapping("/{id}")
    public UserDto update(@PathVariable Long id,
                          @RequestBody UserDto userDto) {
        log.info("Сервер принял запрос \"Обновить данные пользователя по ID\". " +
                "userID={}, RequestBody={}", id, userDto);
        return userService.update(id, userDto);
    }

    @DeleteMapping("/{id}")
    public void remove(@PathVariable Long id) {
        log.info("Сервер принял запрос \"Удалить пользователя по ID\". " +
                "userID={}", id);
        userService.remove(id);
    }
}
