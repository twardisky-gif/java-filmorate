package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventOperation;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.event.EventStorage;
import ru.yandex.practicum.filmorate.storage.friendship.FriendshipStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.List;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;
    private final FriendshipStorage friendshipStorage;
    private final EventStorage eventStorage;

    public UserServiceImpl(@Qualifier("userDbStorage") UserStorage userStorage,
                           FriendshipStorage friendshipStorage,
                           EventStorage eventStorage) {
        this.userStorage = userStorage;
        this.friendshipStorage = friendshipStorage;
        this.eventStorage = eventStorage;
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
        if (friendshipStorage.add(userId, friendId)) {
            eventStorage.add(userId, EventType.FRIEND, EventOperation.ADD, friendId);
            log.info("Пользователь {} добавил в друзья пользователя {}", userId, friendId);
        }
    }

    @Override
    public void removeFriend(long userId, long friendId) {
        getUserOrThrow(userId);
        getUserOrThrow(friendId);
        if (friendshipStorage.remove(userId, friendId)) {
            eventStorage.add(userId, EventType.FRIEND, EventOperation.REMOVE, friendId);
            log.info("Пользователь {} удалил из друзей пользователя {}", userId, friendId);
        }
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

    @Override
    public List<Event> getFeed(long userId) {
        getUserOrThrow(userId);
        return eventStorage.findByUserId(userId);
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
