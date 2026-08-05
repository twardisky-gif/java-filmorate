package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ReviewDto(long reviewId,
                        @NotBlank(message = "Содержание отзыва не может быть пустым") String content,
                        @NotNull(message = "Тип отзыва должен быть указан") Boolean isPositive,
                        @NotNull(message = "Идентификатор пользователя должен быть указан") Long userId,
                        @NotNull(message = "Идентификатор фильма должен быть указан") Long filmId,
                        int useful) {
}
