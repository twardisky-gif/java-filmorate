package ru.yandex.practicum.filmorate.controller;

import ru.yandex.practicum.filmorate.dto.DirectorDto;

import java.util.List;

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
    DirectorDto create(DirectorDto director);

    /**
     * Обновляет существующего режиссёра.
     *
     * @param director данные режиссёра с идентификатором
     * @return обновлённый режиссёр
     * @throws ru.yandex.practicum.filmorate.exception.NotFoundException если режиссёр не найден
     */
    DirectorDto update(DirectorDto director);

    /**
     * Удаляет режиссёра.
     *
     * @param id идентификатор режиссёра
     * @throws ru.yandex.practicum.filmorate.exception.NotFoundException если режиссёр не найден
     */
    void delete(long id);
}
