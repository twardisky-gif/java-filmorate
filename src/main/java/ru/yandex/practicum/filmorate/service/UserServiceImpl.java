package ru.yandex.practicum.filmorate.service;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    public UserServiceImpl(UserStorage userStorage) {
        this.userStorage = userStorage;
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
    public User getById(long id) {
        return getUserOrThrow(id);
    }

    @Override
    public Collection<User> getAll() {
        return userStorage.getAll();
    }

    @Override
    public void addFriend(long userId, long friendId) {
        User user = getUserOrThrow(userId);
        User friend = getUserOrThrow(friendId);
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
        log.info("Пользователи {} и {} стали друзьями", userId, friendId);
    }

    @Override
    public void removeFriend(long userId, long friendId) {
        User user = getUserOrThrow(userId);
        User friend = getUserOrThrow(friendId);
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
        log.info("Пользователи {} и {} больше не друзья", userId, friendId);
    }

    @Override
    public List<User> getFriends(long userId) {
        User user = getUserOrThrow(userId);
        return user.getFriends().stream()
                .map(this::getUserOrThrow)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getCommonFriends(long userId, long otherId) {
        User user = getUserOrThrow(userId);
        User other = getUserOrThrow(otherId);
        Set<Long> userFriends = user.getFriends();
        Set<Long> otherFriends = other.getFriends();
        return userFriends.stream()
                .filter(otherFriends::contains)
                .map(this::getUserOrThrow)
                .collect(Collectors.toList());
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
