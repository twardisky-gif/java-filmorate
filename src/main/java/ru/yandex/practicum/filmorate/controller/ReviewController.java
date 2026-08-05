package ru.yandex.practicum.filmorate.controller;

import ru.yandex.practicum.filmorate.dto.ReviewDto;

import java.util.List;

/**
 * Операции над отзывами на фильмы и оценкой их полезности.
 */
public interface ReviewController {

    /**
     * Создаёт новый отзыв на фильм.
     *
     * @param review данные отзыва
     * @return сохранённый отзыв с присвоенным идентификатором
     * @throws ru.yandex.practicum.filmorate.exception.ValidationException если отзыв пользователя на фильм уже существует
     * @throws ru.yandex.practicum.filmorate.exception.NotFoundException   если пользователь или фильм не найден
     */
    ReviewDto create(ReviewDto review);

    /**
     * Обновляет содержание и оценку существующего отзыва.
     *
     * @param review данные отзыва с заполненным идентификатором
     * @return обновлённый отзыв
     * @throws ru.yandex.practicum.filmorate.exception.NotFoundException если отзыв не найден
     */
    ReviewDto update(ReviewDto review);

    /**
     * Удаляет отзыв по идентификатору.
     *
     * @param id идентификатор отзыва
     * @throws ru.yandex.practicum.filmorate.exception.NotFoundException если отзыв не найден
     */
    void delete(long id);

    /**
     * Возвращает отзыв по идентификатору.
     *
     * @param id идентификатор отзыва
     * @return найденный отзыв
     * @throws ru.yandex.practicum.filmorate.exception.NotFoundException если отзыв не найден
     */
    ReviewDto getById(long id);

    /**
     * Возвращает список отзывов, отсортированных по полезности.
     *
     * @param filmId идентификатор фильма для фильтрации; если {@code null}, возвращаются все отзывы
     * @param count  максимальное количество отзывов в выборке
     * @return список отзывов
     * @throws ru.yandex.practicum.filmorate.exception.NotFoundException если указанный фильм не найден
     */
    List<ReviewDto> getReviews(Long filmId, int count);

    /**
     * Отмечает отзыв как полезный от имени пользователя.
     *
     * @param id     идентификатор отзыва
     * @param userId идентификатор пользователя
     * @throws ru.yandex.practicum.filmorate.exception.NotFoundException если отзыв или пользователь не найден
     */
    void addLike(long id, long userId);

    /**
     * Отмечает отзыв как бесполезный от имени пользователя.
     *
     * @param id     идентификатор отзыва
     * @param userId идентификатор пользователя
     * @throws ru.yandex.practicum.filmorate.exception.NotFoundException если отзыв или пользователь не найден
     */
    void addDislike(long id, long userId);

    /**
     * Снимает отметку «полезно» у отзыва. Операция идемпотентна.
     *
     * @param id     идентификатор отзыва
     * @param userId идентификатор пользователя
     * @throws ru.yandex.practicum.filmorate.exception.NotFoundException если отзыв или пользователь не найден
     */
    void removeLike(long id, long userId);

    /**
     * Снимает отметку «бесполезно» у отзыва. Операция идемпотентна.
     *
     * @param id     идентификатор отзыва
     * @param userId идентификатор пользователя
     * @throws ru.yandex.practicum.filmorate.exception.NotFoundException если отзыв или пользователь не найден
     */
    void removeDislike(long id, long userId);
}
