package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto add(UserDto userDto);

    List<UserDto> getAll();

    UserDto getById(Long id);

    UserDto update(Long id, UserDto userDto);

    void remove(Long id);
}
