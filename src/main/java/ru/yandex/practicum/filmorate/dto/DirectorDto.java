package ru.yandex.practicum.filmorate.dto;

/**
 * Данные режиссёра для ответа API.
 *
 * @param id идентификатор режиссёра
 * @param name имя режиссёра
 */
public record DirectorDto(long id, String name) {
}
