package ru.yandex.practicum.filmorate.storage.film;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.BaseRepository;
import ru.yandex.practicum.filmorate.storage.mappers.FilmRowMapper;

@Repository
@Qualifier("filmDbStorage")
public class FilmDbStorage extends BaseRepository<Film> implements FilmStorage {
    private static final String SELECT_FILM_COLUMNS =
            "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id, m.name AS mpa_name "
                    + "FROM films f "
                    + "JOIN mpa m ON m.mpa_id = f.mpa_id ";
    private static final String FIND_ALL_QUERY = SELECT_FILM_COLUMNS + "ORDER BY f.film_id";
    private static final String FIND_BY_ID_QUERY = SELECT_FILM_COLUMNS + "WHERE f.film_id = ?";
    private static final String FIND_POPULAR_QUERY = SELECT_FILM_COLUMNS
            + "LEFT JOIN likes l ON l.film_id = f.film_id "
            + "GROUP BY f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id, m.name "
            + "ORDER BY COUNT(l.user_id) DESC, f.film_id "
            + "LIMIT ?";
    private static final String INSERT_QUERY =
            "INSERT INTO films (name, description, release_date, duration, mpa_id) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_QUERY =
            "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? "
                    + "WHERE film_id = ?";
    private static final String DELETE_QUERY =
            "DELETE FROM films WHERE film_id = ?";
    private static final String DELETE_GENRES_QUERY =
            "DELETE FROM film_genres WHERE film_id = ?";
    private static final String INSERT_GENRE_QUERY =
            "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
    private static final String FIND_GENRES_BY_FILM_IDS_QUERY =
            "SELECT fg.film_id, g.genre_id, g.name "
                    + "FROM film_genres fg "
                    + "JOIN genres g ON g.genre_id = fg.genre_id "
                    + "WHERE fg.film_id IN (%s) "
                    + "ORDER BY fg.film_id, g.genre_id";
    private static final String FIND_RECOMMENDATIONS_QUERY =
            "WITH similar_users AS (" +
                    "    SELECT l2.user_id, COUNT(*) as common_likes " +
                    "    FROM likes l1 " +
                    "    JOIN likes l2 ON l1.film_id = l2.film_id " +
                    "    WHERE l1.user_id = ? AND l2.user_id != ? " +
                    "    GROUP BY l2.user_id " +
                    "    HAVING COUNT(*) > 0 " +
                    "    ORDER BY common_likes DESC, l2.user_id " +
                    "    LIMIT 1" +
                    ")" +
                    "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, " +
                    "       f.mpa_id, m.name AS mpa_name " +
                    "FROM films f " +
                    "JOIN mpa m ON m.mpa_id = f.mpa_id " +
                    "WHERE f.film_id IN (" +
                    "    SELECT DISTINCT l.film_id " +
                    "    FROM likes l " +
                    "    WHERE l.user_id IN (SELECT user_id FROM similar_users) " +
                    "    AND l.film_id NOT IN (" +
                    "        SELECT film_id FROM likes WHERE user_id = ?" +
                    "    )" +
                    ") " +
                    "ORDER BY f.film_id " +
                    "LIMIT ?";

    public FilmDbStorage(JdbcTemplate jdbc, FilmRowMapper mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Film add(Film film) {
        long id = insert(INSERT_QUERY,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa().getId());
        film.setId(id);
        saveGenres(film);
        return film;
    }

    @Override
    public Film update(Film film) {
        update(UPDATE_QUERY,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());
        jdbc.update(DELETE_GENRES_QUERY, film.getId());
        saveGenres(film);
        return film;
    }

    @Override
    public void delete(long id) {
        delete(DELETE_QUERY, id);
    }

    @Override
    public Optional<Film> getById(long id) {
        Optional<Film> film = findOne(FIND_BY_ID_QUERY, id);
        film.ifPresent(value -> loadGenres(List.of(value)));
        return film;
    }

    @Override
    public Collection<Film> getAll() {
        List<Film> films = findMany(FIND_ALL_QUERY);
        loadGenres(films);
        return films;
    }

    @Override
    public List<Film> getPopular(int count) {
        List<Film> films = findMany(FIND_POPULAR_QUERY, count);
        loadGenres(films);
        return films;
    }

    private void saveGenres(Film film) {
        List<Integer> genreIds = film.getGenres().stream()
                .map(Genre::getId)
                .distinct()
                .sorted()
                .toList();
        if (genreIds.isEmpty()) {
            return;
        }
        long filmId = film.getId();
        jdbc.batchUpdate(INSERT_GENRE_QUERY, genreIds, genreIds.size(),
                (ps, genreId) -> {
                    ps.setLong(1, filmId);
                    ps.setInt(2, genreId);
                });
    }

    private void loadGenres(List<Film> films) {
        if (films.isEmpty()) {
            return;
        }
        Map<Long, Film> filmsById = new HashMap<>();
        for (Film film : films) {
            film.setGenres(new LinkedHashSet<>());
            filmsById.put(film.getId(), film);
        }
        List<Object> filmIds = new ArrayList<>(filmsById.keySet());
        String placeholders = String.join(",", Collections.nCopies(filmIds.size(), "?"));
        jdbc.query(FIND_GENRES_BY_FILM_IDS_QUERY.formatted(placeholders), rs -> {
            Film film = filmsById.get(rs.getLong("film_id"));
            if (film != null) {
                film.getGenres().add(new Genre(rs.getInt("genre_id"), rs.getString("name")));
            }
        }, filmIds.toArray());
    }

    @Override
    public List<Film> getRecommendations(long userId, int limit) {
        List<Film> films = findMany(FIND_RECOMMENDATIONS_QUERY, userId, userId, userId, limit);
        loadGenres(films);
        return films;
    }

}
