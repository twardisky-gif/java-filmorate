package ru.yandex.practicum.filmorate.mapper;

import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public final class GenreMapper {

    private GenreMapper() {
    }

    public static GenreDto toDto(Genre genre) {
        return new GenreDto(genre.getId(), genre.getName());
    }

    public static Genre toModel(GenreDto genre) {
        return new Genre(genre.id(), genre.name());
    }

    public static List<GenreDto> toDto(List<Genre> genres) {
        return genres.stream()
                .map(GenreMapper::toDto)
                .toList();
    }
}
