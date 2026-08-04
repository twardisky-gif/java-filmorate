package ru.yandex.practicum.filmorate.storage.film;

import java.util.*;
import java.util.stream.Collectors;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private long nextId = 1;
    private final Map<Long, Set<Long>> filmLikes = new HashMap<>();

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

    @Override
    public List<Film> getRecommendations(long userId, int limit) {
        Set<Long> userLikes = getLikesByUser(userId);

        Map<Long, Long> similarity = new HashMap<>();
        for (Map.Entry<Long, Set<Long>> entry : filmLikes.entrySet()) {
            for (Long otherUserId : entry.getValue()) {
                if (otherUserId == userId) continue;
                Set<Long> otherLikes = getLikesByUser(otherUserId);
                long intersection = userLikes.stream()
                        .filter(otherLikes::contains)
                        .count();
                if (intersection > 0) {
                    similarity.merge(otherUserId, intersection, Long::sum);
                }
            }
        }

        Long similarUserId = similarity.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        if (similarUserId == null) {
            return List.of();
        }

        Set<Long> similarLikes = getLikesByUser(similarUserId);

        return similarLikes.stream()
                .filter(filmId -> !userLikes.contains(filmId))
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
    }

    public void removeLike(long filmId, long userId) {
        filmLikes.computeIfAbsent(filmId, k -> new HashSet<>()).remove(userId);
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
                    return Long.compare(likes2, likes1); // По убыванию
                })
                .collect(Collectors.toList());
    }

    private Set<Long> getLikesByUser(long userId) {
        return filmLikes.entrySet().stream()
                .filter(entry -> entry.getValue().contains(userId))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }
}