package ru.yandex.practicum.filmorate.controller;

import ru.yandex.practicum.filmorate.dto.EventDto;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.UserDto;

import java.util.Collection;
import java.util.List;

/**
 * Операции над пользователями и их друзьями.
 */
public interface UserController {

    /**
     * Возвращает всех пользователей.
     *
     * @return коллекция пользователей
     */
    Collection<UserDto> getAll();

    /**
     * Возвращает пользователя по идентификатору.
     *
     * @param id идентификатор пользователя
     * @return найденный пользователь
     * @throws ru.yandex.practicum.filmorate.exception.NotFoundException если пользователь не найден
     */
    UserDto getById(long id);

    /**
     * Создаёт нового пользователя.
     *
     * @param user данные пользователя
     * @return сохранённый пользователь с присвоенным идентификатором
     */
    UserDto create(UserDto user);

    /**
     * Обновляет существующего пользователя.
     *
     * @param user данные пользователя с заполненным идентификатором
     * @return обновлённый пользователь
     * @throws ru.yandex.practicum.filmorate.exception.NotFoundException если пользователь не найден
     */
    UserDto update(UserDto user);

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
    List<UserDto> getFriends(long id);

    /**
     * Возвращает общих друзей двух пользователей.
     *
     * @param id      идентификатор первого пользователя
     * @param otherId идентификатор второго пользователя
     * @return список общих друзей
     */
    List<UserDto> getCommonFriends(long id, long otherId);

    /**
     * Возвращает рекомендации фильмов для пользователя.
     *
     * @param id    идентификатор пользователя
     * @param count максимальное количество рекомендаций
     * @return список рекомендованных фильмов
     * @throws ru.yandex.practicum.filmorate.exception.NotFoundException если пользователь не найден
     */
    List<FilmDto> getRecommendations(long id, int count);

    /**
     * Возвращает ленту событий пользователя.
     *
     * @param id идентификатор пользователя
     * @return список событий, отсортированный по времени
     * @throws ru.yandex.practicum.filmorate.exception.NotFoundException если пользователь не найден
     */
    List<EventDto> getFeed(long id);
}
