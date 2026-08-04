package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class Review {

    private long reviewId;

    @NotBlank(message = "Содержание отзыва не может быть пустым")
    private String content;

    @NotNull(message = "Необходимо указать, положительный отзыв или отрицательный")
    private Boolean isPositive;

    @NotNull(message = "Идентификатор пользователя должен быть указан")
    private Long userId;

    @NotNull(message = "Идентификатор фильма должен быть указан")
    private Long filmId;

    private int useful;
}
