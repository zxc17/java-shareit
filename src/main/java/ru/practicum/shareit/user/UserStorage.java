package ru.practicum.shareit.user;

import java.util.List;

public interface UserStorage {
    User add(User user);

    List<User> getAll();

    User getById(Long id);

    User update(User newUser, Long id);

    void remove(Long id);

    boolean isEmailUsed(String email, Long userId);
}
