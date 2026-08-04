package ru.yandex.practicum.filmorate.storage.friendship;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface FriendshipStorage {
    void add(long userId, long friendId);

    void remove(long userId, long friendId);

    List<User> getFriends(long userId);

    List<User> getCommonFriends(long userId, long otherId);
}
