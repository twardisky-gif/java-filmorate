package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.Set;

public record FilmDto(long id,
                      @NotBlank(message = "Название не может быть пустым") String name,
                      @Size(max = 200, message = "Максимальная длина описания 200 символов") String description,
                      LocalDate releaseDate,
                      @Positive(message = "Продолжительность фильма должна быть положительным числом") int duration,
                      @NotNull(message = "Рейтинг MPA должен быть указан") MpaDto mpa,
                      Set<GenreDto> genres,
                      Set<DirectorDto> directors) {
}
