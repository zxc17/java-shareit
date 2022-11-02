package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ValidationNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserDto add(UserDto userDto) {
        User user = userMapper.toUser(userDto);
        user = userRepository.save(user);
        return userMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> getAll() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ValidationNotFoundException(String.format("userId=%s не найден.", id)));
        return userMapper.toUserDto(user);
    }

    @Override
    public UserDto update(Long id, UserDto userDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ValidationNotFoundException(String.format("userId=%s не найден.", id)));
        if (userDto.getName() != null)
            user.setName(userDto.getName());
        if (userDto.getEmail() != null)
            user.setEmail(userDto.getEmail());
        user = userRepository.save(user);
        return userMapper.toUserDto(user);
    }

    @Override
    public void remove(Long id) {
        if (!userRepository.existsById(id)) throw new ValidationNotFoundException(String
                .format("userId=%s не найден.", id));
        userRepository.deleteById(id);
    }

}
