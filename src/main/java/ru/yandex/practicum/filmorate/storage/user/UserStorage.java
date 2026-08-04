package ru.yandex.practicum.filmorate.storage.user;

import java.util.Collection;
import java.util.Optional;

import ru.yandex.practicum.filmorate.model.User;

public interface UserStorage {
    User add(User user);

    User update(User user);

    boolean delete(long id);

    Optional<User> getById(long id);

    Collection<User> getAll();
}
