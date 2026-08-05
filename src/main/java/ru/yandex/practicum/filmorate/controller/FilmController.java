package ru.yandex.practicum.filmorate.controller;

import ru.yandex.practicum.filmorate.dto.FilmDto;

import java.util.Collection;
import java.util.List;

/**
 * Операции над фильмами и лайками.
 */
public interface FilmController {

    /**
     * Возвращает все фильмы.
     *
     * @return коллекция фильмов
     */
    Collection<FilmDto> getAll();

    /**
     * Возвращает фильм по идентификатору.
     *
     * @param id идентификатор фильма
     * @return найденный фильм
     * @throws ru.yandex.practicum.filmorate.exception.NotFoundException если фильм не найден
     */
    FilmDto getById(long id);

    /**
     * Добавляет новый фильм.
     *
     * @param film данные фильма
     * @return сохранённый фильм с присвоенным идентификатором
     */
    FilmDto create(FilmDto film);

    /**
     * Обновляет существующий фильм.
     *
     * @param film данные фильма с заполненным идентификатором
     * @return обновлённый фильм
     * @throws ru.yandex.practicum.filmorate.exception.NotFoundException если фильм не найден
     */
    FilmDto update(FilmDto film);

    /**
     * Ставит лайк фильму от имени пользователя.
     *
     * @param id     идентификатор фильма
     * @param userId идентификатор пользователя
     */
    void addLike(long id, long userId);

    /**
     * Удаляет фильм.
     *
     * @param id идентификатор фильма
     */
    void removeFilm(long id);

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
     * @param count   размер выборки
     * @param genreId идентификатор жанра по которому хотим фильтровать
     * @param year    год фильма по которому хотим фильтровать
     * @return список фильмов по убыванию популярности
     */
    List<FilmDto> getPopular(int count, Integer genreId, Integer year);

    /**
     * Возвращает фильмы режиссёра с указанной сортировкой.
     *
     * @param directorId идентификатор режиссёра
     * @param sortBy     тип сортировки: по году или количеству лайков
     * @return список фильмов режиссёра
     * @throws ru.yandex.practicum.filmorate.exception.NotFoundException если режиссёр не найден
     * @throws ru.yandex.practicum.filmorate.exception.ValidationException если тип сортировки не поддерживается
     */
    List<FilmDto> getByDirector(long directorId, String sortBy);

    /**
     * Ищет фильмы по названию, режиссёру или обоим полям.
     *
     * @param query поисковая строка
     * @param by    поля поиска: title, director или оба значения
     * @return список найденных фильмов по убыванию популярности
     * @throws ru.yandex.practicum.filmorate.exception.ValidationException если указано неподдерживаемое поле поиска
     */
    List<FilmDto> search(String query, String by);

    /**
     * Возвращает общие фильмы двух пользователей по убыванию популярности.
     *
     * @param userId   идентификатор первого пользователя
     * @param friendId идентификатор второго пользователя
     * @return список общих фильмов
     * @throws ru.yandex.practicum.filmorate.exception.NotFoundException если один из пользователей не найден
     */
    List<FilmDto> getCommonFilms(long userId, long friendId);
}
