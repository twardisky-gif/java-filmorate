package ru.yandex.practicum.filmorate.dto;

/**
 * Жанр фильма в ответе API.
 *
 * @param id идентификатор жанра
 * @param name название жанра
 */
public record GenreDto(Integer id, String name) {
}
