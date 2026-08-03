package ru.yandex.practicum.filmorate.mapper;

import java.util.List;

import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.model.Genre;

public final class GenreMapper {

    private GenreMapper() {
    }

    public static GenreDto toDto(Genre genre) {
        return new GenreDto(genre.getId(), genre.getName());
    }

    public static List<GenreDto> toDto(List<Genre> genres) {
        return genres.stream()
                .map(GenreMapper::toDto)
                .toList();
    }
}
