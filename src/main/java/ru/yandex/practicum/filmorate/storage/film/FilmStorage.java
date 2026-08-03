package ru.yandex.practicum.filmorate.storage.film;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import ru.yandex.practicum.filmorate.model.Film;

public interface FilmStorage {
    Film add(Film film);

    Film update(Film film);

    void delete(long id);

    Optional<Film> getById(long id);

    Collection<Film> getAll();

    List<Film> getPopular(int count);
    List<Film> getRecommendations(long userId, int limit);
}
