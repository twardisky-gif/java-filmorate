package ru.yandex.practicum.filmorate.service;

import java.util.Collection;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.friendship.FriendshipStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;
    private final FriendshipStorage friendshipStorage;

    public UserServiceImpl(@Qualifier("userDbStorage") UserStorage userStorage,
                           FriendshipStorage friendshipStorage) {
        this.userStorage = userStorage;
        this.friendshipStorage = friendshipStorage;
    }

    @Override
    public User create(User user) {
        applyNameIfEmpty(user);
        User created = userStorage.add(user);
        log.info("Создан пользователь: {}", created.getLogin());
        return created;
    }

    @Override
    public User update(User user) {
        getUserOrThrow(user.getId());
        applyNameIfEmpty(user);
        User updated = userStorage.update(user);
        log.info("Обновлен пользователь: {}", updated.getLogin());
        return updated;
    }

    @Override
    public void removeUser(Long id) {
        boolean isRemove = userStorage.delete(id);
        if (!isRemove) {
            throw new NotFoundException("Пользователь с id=" + id + " не найден");
        }
    }

    @Override
    public User getById(long id) {
        return getUserOrThrow(id);
    }

    @Override
    public Collection<User> getAll() {
        return userStorage.getAll();
    }

    @Override
    public void addFriend(long userId, long friendId) {
        getUserOrThrow(userId);
        getUserOrThrow(friendId);
        friendshipStorage.add(userId, friendId);
        log.info("Пользователь {} добавил в друзья пользователя {}", userId, friendId);
    }

    @Override
    public void removeFriend(long userId, long friendId) {
        getUserOrThrow(userId);
        getUserOrThrow(friendId);
        friendshipStorage.remove(userId, friendId);
        log.info("Пользователь {} удалил из друзей пользователя {}", userId, friendId);
    }

    @Override
    public List<User> getFriends(long userId) {
        getUserOrThrow(userId);
        return friendshipStorage.getFriends(userId);
    }

    @Override
    public List<User> getCommonFriends(long userId, long otherId) {
        getUserOrThrow(userId);
        getUserOrThrow(otherId);
        return friendshipStorage.getCommonFriends(userId, otherId);
    }

    private void applyNameIfEmpty(User user) {
        String name = user.getName();
        if (name == null || name.isBlank()) {
            user.setName(user.getLogin());
        }
    }

    private User getUserOrThrow(long id) {
        return userStorage.getById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + id + " не найден"));
    }
}
