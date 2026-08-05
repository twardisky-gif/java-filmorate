package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Данные режиссёра для ответа API.
 *
 * @param id   идентификатор режиссёра
 * @param name имя режиссёра
 */
public record DirectorDto(long id,
                          @NotBlank(message = "Имя режиссёра не может быть пустым") String name) {
}
