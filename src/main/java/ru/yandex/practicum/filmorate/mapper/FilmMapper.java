package ru.yandex.practicum.filmorate.mapper;

import ru.yandex.practicum.filmorate.dto.DirectorDto;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public final class FilmMapper {
    private FilmMapper() {
    }

    public static FilmDto toDto(Film film) {
        Set<GenreDto> genres = film.getGenres().stream()
                .map(GenreMapper::toDto)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        Set<DirectorDto> directors = film.getDirectors().stream()
                .map(DirectorMapper::toDto)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        return new FilmDto(film.getId(), film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration(), MpaMapper.toDto(film.getMpa()), genres, directors);
    }

    public static List<FilmDto> toDto(Collection<Film> films) {
        return films.stream()
                .map(FilmMapper::toDto)
                .toList();
    }

    public static Film toModel(FilmDto filmDto) {
        Film film = new Film();
        film.setId(filmDto.id());
        film.setName(filmDto.name());
        film.setDescription(filmDto.description());
        film.setReleaseDate(filmDto.releaseDate());
        film.setDuration(filmDto.duration());
        film.setMpa(filmDto.mpa() == null ? null : MpaMapper.toModel(filmDto.mpa()));
        film.setGenres(filmDto.genres() == null ? new LinkedHashSet<>() : filmDto.genres().stream()
                .map(GenreMapper::toModel)
                .collect(Collectors.toCollection(LinkedHashSet::new)));
        film.setDirectors(filmDto.directors() == null ? new LinkedHashSet<>() : filmDto.directors().stream()
                .map(DirectorMapper::toModel)
                .collect(Collectors.toCollection(LinkedHashSet::new)));
        return film;
    }
}
