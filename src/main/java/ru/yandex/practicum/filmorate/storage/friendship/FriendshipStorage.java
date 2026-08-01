package ru.yandex.practicum.filmorate.storage.friendship;

import java.util.List;

import ru.yandex.practicum.filmorate.model.User;

public interface FriendshipStorage {
    void add(long userId, long friendId);

    void remove(long userId, long friendId);

    List<User> getFriends(long userId);

    List<User> getCommonFriends(long userId, long otherId);
}
