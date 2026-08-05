package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    Film add(Film film);

    Film update(Film film);

    boolean delete(long id);

    Optional<Film> getById(long id);

    Collection<Film> getAll();

    List<Film> getPopular(int count, Integer genreId, Integer year);

    List<Film> getRecommendations(long userId, int limit);

    List<Film> getByDirector(long directorId, String sortBy);

    List<Film> search(String query, boolean byTitle, boolean byDirector);

    List<Film> getCommonFilms(long userId, long friendId);
}
