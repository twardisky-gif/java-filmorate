package ru.yandex.practicum.filmorate.controller;

import java.util.List;

import ru.yandex.practicum.filmorate.dto.MpaDto;

/**
 * Операции над справочником возрастных рейтингов MPA.
 */
public interface MpaController {

    /**
     * Возвращает все рейтинги.
     *
     * @return список рейтингов
     */
    List<MpaDto> getAll();

    /**
     * Возвращает рейтинг по идентификатору.
     *
     * @param id идентификатор рейтинга
     * @return найденный рейтинг
     * @throws ru.yandex.practicum.filmorate.exception.NotFoundException если рейтинг не найден
     */
    MpaDto getById(int id);
}
