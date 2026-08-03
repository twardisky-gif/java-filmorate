package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class Film {
    private long id;
    @NotBlank(message = "Название не может быть пустым")
    private String name;
    @Size(max = 200, message = "Максимальная длина описания 200 символов")
    private String description;
    private LocalDate releaseDate;
    @Positive(message = "Продолжительность фильма должна быть положительным числом")
    private int duration;
    @NotNull(message = "Рейтинг MPA должен быть указан")
    private Mpa mpa;
    private Set<Genre> genres = new LinkedHashSet<>();
    private Set<Director> directors = new LinkedHashSet<>();
}
