package ru.practicum.shareit.user;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.practicum.shareit.exception.ValidationNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.WARN)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    private UserServiceImpl userService;
    private UserMapper userMapper;
    @Captor
    ArgumentCaptor<User> userCaptor;

    @BeforeEach
    void setUp() {
        userMapper = new UserMapper();
        userService = new UserServiceImpl(userRepository, userMapper);
    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void add() {
        // Assign
        User user = User.builder()
                .id(1L)
                .email("test@ya.ru")
                .name("test")
                .build();
        when(userRepository.save(any())).then(invocationOnMock -> invocationOnMock.getArgument(0));

        // Act
        UserDto result = userService.add(userMapper.toUserDto(user));

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("test@ya.ru", result.getEmail());
        assertEquals("test", result.getName());
        verify(userRepository).save(user);
    }

    @Test
    void getAll() {
        // Assign
        User user1 = User.builder()
                .id(1L)
                .email("test@ya.ru")
                .name("test")
                .build();
        User user2 = User.builder()
                .id(2L)
                .email("t2@ya.ru")
                .name("second")
                .build();
        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        // Act
        List<UserDto> result = userService.getAll();

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.get(0).getId());
        assertEquals("test@ya.ru", result.get(0).getEmail());
        assertEquals("test", result.get(0).getName());
        assertEquals(2L, result.get(1).getId());
        assertEquals("t2@ya.ru", result.get(1).getEmail());
        assertEquals("second", result.get(1).getName());
        verify(userRepository).findAll();
    }

    @Test
    void getById_ok() {
        // Assign
        User user1 = User.builder()
                .id(1L)
                .email("test@ya.ru")
                .name("test")
                .build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));

        // Act
        var result = userService.getById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("test@ya.ru", result.getEmail());
        assertEquals("test", result.getName());
        verify(userRepository).findById(1L);
    }

    @Test
    void getById_fail() {
        // Assign
        when(userRepository.findById(0L)).thenThrow(new ValidationNotFoundException("userId=test не найден."));

        // Act

        // Assert
        var e = assertThrows(ValidationNotFoundException.class, () -> userRepository.findById(0L));
        assertEquals("userId=test не найден.", e.getMessage());
        verify(userRepository).findById(0L);
    }

    @Test
    void update_ok() {
        // Assign
        User user = User.builder()
                .id(1L)
                .email("test@ya.ru")
                .name("test")
                .build();
        User updatedUser = User.builder()
                .id(1L)
                .email("update@ya.ru")
                .name("update")
                .build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(updatedUser)).thenReturn(updatedUser);

        // Act
        var result = userService.update(1L, userMapper.toUserDto(updatedUser));

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("update@ya.ru", result.getEmail());
        assertEquals("update", result.getName());
        verify(userRepository).findById(1L);
        verify(userRepository).save(userCaptor.capture());
        assertEquals(updatedUser, userCaptor.getValue());
    }

    @Test
    void update_fail() {
        // Assign
        User user = new User();
        when(userRepository.findById(0L)).thenThrow(new ValidationNotFoundException("userId=test не найден."));

        // Act

        // Assert
        var e = assertThrows(
                ValidationNotFoundException.class, () -> userService.update(0L, userMapper.toUserDto(user)));
        assertEquals("userId=test не найден.", e.getMessage());
        verify(userRepository).findById(0L);
    }

    @Test
    void remove_ok() {
        // Assign
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);

        // Act
        userService.remove(1L);

        // Assert
        verify(userRepository).existsById(1L);
        verify(userRepository).deleteById(1L);
    }

    @Test
    void remove_fail() {
        // Assign
        when(userRepository.existsById(0L)).thenThrow(new ValidationNotFoundException("userId=test не найден."));

        // Act
        var e = assertThrows(ValidationNotFoundException.class, () -> userService.remove(0L));

        // Assert
        assertEquals("userId=test не найден.", e.getMessage());
        verify(userRepository).existsById(0L);
    }
}