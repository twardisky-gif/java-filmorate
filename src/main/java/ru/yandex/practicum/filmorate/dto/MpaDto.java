package ru.yandex.practicum.filmorate.dto;

/**
 * Возрастной рейтинг MPA в ответе API.
 *
 * @param id идентификатор рейтинга
 * @param name название рейтинга
 */
public record MpaDto(Integer id, String name) {
}
