package ru.yandex.practicum.filmorate.controller;

import java.util.Collection;
import java.util.List;

import ru.yandex.practicum.filmorate.model.User;

/**
 * Операции над пользователями и их друзьями.
 */
public interface UserController {

    /**
     * Возвращает всех пользователей.
     *
     * @return коллекция пользователей
     */
    Collection<User> getAll();

    /**
     * Возвращает пользователя по идентификатору.
     *
     * @param id идентификатор пользователя
     * @return найденный пользователь
     * @throws ru.yandex.practicum.filmorate.exception.NotFoundException если пользователь не найден
     */
    User getById(long id);

    /**
     * Создаёт нового пользователя.
     *
     * @param user данные пользователя
     * @return сохранённый пользователь с присвоенным идентификатором
     */
    User create(User user);

    /**
     * Обновляет существующего пользователя.
     *
     * @param user данные пользователя с заполненным идентификатором
     * @return обновлённый пользователь
     * @throws ru.yandex.practicum.filmorate.exception.NotFoundException если пользователь не найден
     */
    User update(User user);

    /**
     * Удаляет пользователя.
     *
     * @param id идентификатор пользователя
     */
    void removeUser(long id);

    /**
     * Добавляет пользователя в друзья. Дружба односторонняя.
     *
     * @param id       идентификатор пользователя
     * @param friendId идентификатор добавляемого друга
     */
    void addFriend(long id, long friendId);

    /**
     * Удаляет пользователя из друзей.
     *
     * @param id       идентификатор пользователя
     * @param friendId идентификатор удаляемого друга
     */
    void removeFriend(long id, long friendId);

    /**
     * Возвращает друзей пользователя.
     *
     * @param id идентификатор пользователя
     * @return список друзей
     */
    List<User> getFriends(long id);

    /**
     * Возвращает общих друзей двух пользователей.
     *
     * @param id      идентификатор первого пользователя
     * @param otherId идентификатор второго пользователя
     * @return список общих друзей
     */
    List<User> getCommonFriends(long id, long otherId);
}
