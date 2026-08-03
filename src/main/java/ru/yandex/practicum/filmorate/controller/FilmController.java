package ru.yandex.practicum.filmorate.controller;

import java.util.Collection;
import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.filmorate.model.Film;

/**
 * Операции над фильмами и лайками.
 */
public interface FilmController {

    /**
     * Возвращает все фильмы.
     *
     * @return коллекция фильмов
     */
    Collection<Film> getAll();

    /**
     * Возвращает фильм по идентификатору.
     *
     * @param id идентификатор фильма
     * @return найденный фильм
     * @throws ru.yandex.practicum.filmorate.exception.NotFoundException если фильм не найден
     */
    Film getById(long id);

    /**
     * Добавляет новый фильм.
     *
     * @param film данные фильма
     * @return сохранённый фильм с присвоенным идентификатором
     */
    Film create(Film film);

    /**
     * Обновляет существующий фильм.
     *
     * @param film данные фильма с заполненным идентификатором
     * @return обновлённый фильм
     * @throws ru.yandex.practicum.filmorate.exception.NotFoundException если фильм не найден
     */
    Film update(Film film);

    /**
     * Ставит лайк фильму от имени пользователя.
     *
     * @param id     идентификатор фильма
     * @param userId идентификатор пользователя
     */
    void addLike(long id, long userId);

    /**
     * Удаляет лайк пользователя у фильма.
     *
     * @param id     идентификатор фильма
     * @param userId идентификатор пользователя
     */
    void removeLike(long id, long userId);

    /**
     * Возвращает самые популярные фильмы по количеству лайков.
     *
     * @param count размер выборки
     * @return список фильмов по убыванию популярности
     */
    List<Film> getPopular(int count);

    List<Film> getRecommendations(long userId, int count);
}
