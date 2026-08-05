package ru.yandex.practicum.filmorate.controller;

import ru.yandex.practicum.filmorate.dto.GenreDto;

import java.util.List;

/**
 * Операции над справочником жанров фильмов.
 */
public interface GenreController {

    /**
     * Возвращает все жанры.
     *
     * @return список жанров
     */
    List<GenreDto> getAll();

    /**
     * Возвращает жанр по идентификатору.
     *
     * @param id идентификатор жанра
     * @return найденный жанр
     * @throws ru.yandex.practicum.filmorate.exception.NotFoundException если жанр не найден
     */
    GenreDto getById(int id);
}
