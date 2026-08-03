package ru.yandex.practicum.filmorate.storage.film;

import java.util.*;
import java.util.stream.Collectors;

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
    public List<Film> getRecommendations(long userId, int limit) {
        Map<Long, Long> userIntersection = new HashMap<>();
        Set<Long> userLikes = getLikesByUser(userId);

        for (Map.Entry<Long, Set<Long>> entry : filmLikes.entrySet()) {
            for (Long otherUserId : entry.getValue()) {
                if (otherUserId == userId) continue;
                Set<Long> otherUserLikes = getLikesByUser(otherUserId);
                long intersection = userLikes.stream()
                        .filter(otherUserLikes::contains)
                        .count();
                if (intersection > 0) {
                    userIntersection.merge(otherUserId, intersection, Long::sum);
                }
            }
        }

        Long similarUserId = userIntersection.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        if (similarUserId == null) {
            return List.of();
        }

        Set<Long> similarUserLikes = getLikesByUser(similarUserId);
        Set<Long> userLikesSet = getLikesByUser(userId);

        return similarUserLikes.stream()
                .filter(filmId -> !userLikesSet.contains(filmId))
                .map(films::get)
                .filter(Objects::nonNull)
                .limit(limit)
                .collect(Collectors.toList());
    }

    private Set<Long> getLikesByUser(long userId) {
        return filmLikes.entrySet().stream()
                .filter(entry -> entry.getValue().contains(userId))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    public void addLike(long filmId, long userId) {
        filmLikes.computeIfAbsent(filmId, k -> new HashSet<>()).add(userId);
    }

    public void removeLike(long filmId, long userId) {
        filmLikes.computeIfAbsent(filmId, k -> new HashSet<>()).remove(userId);
    }
}
