package ru.yandex.practicum.filmorate.storage.film;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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
    public List<Film> getPopular(int count) {
        return films.values().stream()
                .sorted(Comparator.comparingLong(Film::getId))
                .limit(count)
                .toList();
    }

    @Override
    public List<Film> getByDirector(long directorId, String sortBy) {
        Comparator<Film> comparator = sortBy.equals("year")
                ? Comparator.comparing(Film::getReleaseDate).thenComparingLong(Film::getId)
                : Comparator.comparingLong(Film::getId);
        return films.values().stream()
                .filter(film -> film.getDirectors().stream()
                        .anyMatch(director -> director.getId() == directorId))
                .sorted(comparator)
                .toList();
    }

    @Override
    public List<Film> search(String query, boolean byTitle, boolean byDirector) {
        String normalizedQuery = query.toLowerCase(Locale.ROOT);
        return films.values().stream()
                .filter(film -> byTitle && film.getName().toLowerCase(Locale.ROOT).contains(normalizedQuery)
                        || byDirector && film.getDirectors().stream()
                        .anyMatch(director -> director.getName().toLowerCase(Locale.ROOT).contains(normalizedQuery)))
                .sorted(Comparator.comparingLong(Film::getId))
                .toList();
    }
}
