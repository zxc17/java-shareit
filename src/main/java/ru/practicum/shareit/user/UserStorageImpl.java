package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class UserStorageImpl implements UserStorage {
    private final Map<Long, User> users;
    private final Map<String, Long> emails; //Для контроля уникальности.
    private long id = 0;

    @Override
    public User add(User user) {
        long newId = getNewId();
        user.setId(newId);
        users.put(newId, user);
        emails.put(user.getEmail(), newId);
        return user;
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getById(Long id) {
        return users.get(id);
    }

    @Override
    public User update(User newUser, Long id) {
        User user = getById(id);
        if (newUser.getName() != null)
            user.setName(newUser.getName());
        if (newUser.getEmail() != null) {
            String oldEmail = getById(id).getEmail();
            // Для контроля уникальности удалить старый email, добавить новый.
            emails.remove(oldEmail);
            emails.put(newUser.getEmail(), id);
            user.setEmail(newUser.getEmail());
        }
        users.put(id, user);
        return user;
    }

    @Override
    public void remove(Long id) {
        emails.remove(getById(id).getEmail());
        users.remove(id);
    }

    private long getNewId() {
        while (users.containsKey(++id)) ;
        return id;
    }

    /**
     * Проверяет уникальность e-mail.
     *
     * @param email  Проверяемый e-mail.
     * @param userId ID пользователя.
     * @return true - если такой e-mail уже используется другим пользователем.
     */
    public boolean isEmailUsed(String email, Long userId) {
        return emails.containsKey(email) && !emails.get(email).equals(userId);
    }
}
