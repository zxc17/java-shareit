package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ValidationConflictException;
import ru.practicum.shareit.exception.ValidationNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    public UserDto add(UserDto userDto) {
        if (userStorage.isEmailUsed(userDto.getEmail(), 0L)) // 0 - новый пользователь.
            throw new ValidationConflictException(String.format("E-mail %s уже используется.", userDto.getEmail()));
        User user = UserMapper.toUser(userDto);
        user = userStorage.add(user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> getAll() {
        List<User> users = userStorage.getAll();
        return users.stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getById(Long id) {
        User user = userStorage.getById(id);
        if (user == null) throw new ValidationNotFoundException(String.format("userId=%s не найден.", id));
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto update(Long id, UserDto userDto) {
        if (userStorage.getById(id) == null) throw new ValidationNotFoundException(String
                .format("userId=%s не найден.", id));
        if (userStorage.isEmailUsed(userDto.getEmail(), id)) throw new ValidationConflictException(String
                .format("E-mail %s уже используется.", userDto.getEmail()));
        User user = UserMapper.toUser(userDto);
        user = userStorage.update(user, id);
        return UserMapper.toUserDto(user);
    }

    @Override
    public void remove(Long id) {
        if (userStorage.getById(id) == null)
            throw new ValidationNotFoundException(String.format("userId=%s не найден.", id));
        userStorage.remove(id);
    }

}
