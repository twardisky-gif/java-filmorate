package ru.yandex.practicum.filmorate.service;

import java.util.Collection;
import java.util.List;

import ru.yandex.practicum.filmorate.model.Film;

public interface FilmService {
    Film create(Film film);

    Film update(Film film);

    void removeFilm(Long id);

    Film getById(long id);

    Collection<Film> getAll();

    void addLike(long filmId, long userId);

    void removeLike(long filmId, long userId);

    List<Film> getPopular(int count, Integer genreId, Integer year);

    List<Film> getByDirector(long directorId, String sortBy);

    List<Film> search(String query, String by);

    List<Film> getRecommendations(long userId, int limit);
}
