package ru.yandex.practicum.filmorate.controller;

import java.util.List;

import ru.yandex.practicum.filmorate.dto.DirectorDto;
import ru.yandex.practicum.filmorate.model.Director;

/**
 * Операции над справочником режиссёров.
 */
public interface DirectorController {

    /**
     * Возвращает всех режиссёров.
     *
     * @return список режиссёров
     */
    List<DirectorDto> getAll();

    /**
     * Возвращает режиссёра по идентификатору.
     *
     * @param id идентификатор режиссёра
     * @return найденный режиссёр
     * @throws ru.yandex.practicum.filmorate.exception.NotFoundException если режиссёр не найден
     */
    DirectorDto getById(long id);

    /**
     * Добавляет нового режиссёра.
     *
     * @param director данные режиссёра
     * @return созданный режиссёр
     */
    DirectorDto create(Director director);

    /**
     * Обновляет существующего режиссёра.
     *
     * @param director данные режиссёра с идентификатором
     * @return обновлённый режиссёр
     * @throws ru.yandex.practicum.filmorate.exception.NotFoundException если режиссёр не найден
     */
    DirectorDto update(Director director);

    /**
     * Удаляет режиссёра.
     *
     * @param id идентификатор режиссёра
     * @throws ru.yandex.practicum.filmorate.exception.NotFoundException если режиссёр не найден
     */
    void delete(long id);
}
