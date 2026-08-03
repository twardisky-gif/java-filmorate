package ru.yandex.practicum.filmorate.storage.film;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private long nextId = 1;

    @Override
    public Film add(Film film) {
        film.setId(nextId++);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film film) {
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public void delete(long id) {
        films.remove(id);
    }

    @Override
    public Optional<Film> getById(long id) {
        return Optional.ofNullable(films.get(id));
    }

    @Override
    public Collection<Film> getAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public List<Film> getPopular(int count, Integer genreId, Integer year) {
        return films.values().stream()
                // Если genreId == null — не фильтруем по жанру
                .filter(film -> genreId == null ||
                        film.getGenres().stream().anyMatch(g -> g.getId() == genreId))
                // Если year == null — не фильтруем по году
                .filter(film -> year == null ||
                        film.getReleaseDate().getYear() == year)
                .sorted(Comparator.comparingLong(Film::getId))
                .limit(count)
                .toList();
    }
}
