package ru.yandex.practicum.filmorate.service;

import java.util.Collection;
import java.util.List;

import ru.yandex.practicum.filmorate.model.User;

public interface UserService {
    User create(User user);

    User update(User user);

    User getById(long id);

    Collection<User> getAll();

    void addFriend(long userId, long friendId);

    void removeFriend(long userId, long friendId);

    List<User> getFriends(long userId);

    List<User> getCommonFriends(long userId, long otherId);
}
