package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private final Map<Long, Set<Long>> filmLikes = new HashMap<>();
    private final Map<Long, Set<Long>> userLikes = new HashMap<>();
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
    public boolean delete(long id) {
        Set<Long> users = filmLikes.remove(id);
        if (users != null) {
            users.forEach(userId -> removeUserLike(userId, id));
        }
        return films.remove(id) != null;
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
                .filter(film -> genreId == null ||
                        film.getGenres().stream().anyMatch(g -> g.getId() == genreId))
                .filter(film -> year == null ||
                        film.getReleaseDate().getYear() == year)
                .sorted(Comparator.comparingLong(Film::getId))
                .limit(count)
                .toList();
    }

    @Override
    public List<Film> getRecommendations(long userId, int limit) {
        Set<Long> likedFilms = getLikesByUser(userId);
        Long similarUserId = userLikes.entrySet().stream()
                .filter(entry -> entry.getKey() != userId)
                .map(entry -> Map.entry(entry.getKey(), countCommonFilms(likedFilms, entry.getValue())))
                .filter(entry -> entry.getValue() > 0)
                .max(Comparator.comparingLong((Map.Entry<Long, Long> entry) -> entry.getValue())
                        .thenComparingLong(entry -> -entry.getKey()))
                .map(Map.Entry::getKey)
                .orElse(null);

        if (similarUserId == null) {
            return List.of();
        }

        Set<Long> similarLikes = getLikesByUser(similarUserId);

        return similarLikes.stream()
                .filter(filmId -> !likedFilms.contains(filmId))
                .map(films::get)
                .filter(Objects::nonNull)
                .limit(limit)
                .collect(Collectors.toList());
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


    public void addLike(long filmId, long userId) {
        filmLikes.computeIfAbsent(filmId, k -> new HashSet<>()).add(userId);
        userLikes.computeIfAbsent(userId, k -> new HashSet<>()).add(filmId);
    }

    public void removeLike(long filmId, long userId) {
        Set<Long> users = filmLikes.get(filmId);
        if (users != null) {
            users.remove(userId);
        }
        removeUserLike(userId, filmId);
    }

    @Override
    public List<Film> getCommonFilms(long userId, long friendId) {
        Set<Long> userLikes = getLikesByUser(userId);
        Set<Long> friendLikes = getLikesByUser(friendId);

        Set<Long> commonFilmIds = new HashSet<>(userLikes);
        commonFilmIds.retainAll(friendLikes);

        return commonFilmIds.stream()
                .map(films::get)
                .filter(Objects::nonNull)
                .sorted((f1, f2) -> {
                    long likes1 = filmLikes.getOrDefault(f1.getId(), Collections.emptySet()).size();
                    long likes2 = filmLikes.getOrDefault(f2.getId(), Collections.emptySet()).size();
                    return Long.compare(likes2, likes1);
                })
                .collect(Collectors.toList());
    }

    private Set<Long> getLikesByUser(long userId) {
        return userLikes.getOrDefault(userId, Collections.emptySet());
    }

    private long countCommonFilms(Set<Long> first, Set<Long> second) {
        return first.stream()
                .filter(second::contains)
                .count();
    }

    private void removeUserLike(long userId, long filmId) {
        Set<Long> likedFilms = userLikes.get(userId);
        if (likedFilms != null) {
            likedFilms.remove(filmId);
            if (likedFilms.isEmpty()) {
                userLikes.remove(userId);
            }
        }
    }
}
