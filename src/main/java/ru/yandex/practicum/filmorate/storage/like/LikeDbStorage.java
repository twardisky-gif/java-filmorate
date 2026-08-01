package ru.yandex.practicum.filmorate.storage.like;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class LikeDbStorage implements LikeStorage {
    private static final String ADD_QUERY =
            "MERGE INTO likes (film_id, user_id) KEY (film_id, user_id) VALUES (?, ?)";
    private static final String REMOVE_QUERY =
            "DELETE FROM likes WHERE film_id = ? AND user_id = ?";

    private final JdbcTemplate jdbc;

    public LikeDbStorage(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public void add(long filmId, long userId) {
        jdbc.update(ADD_QUERY, filmId, userId);
    }

    @Override
    public void remove(long filmId, long userId) {
        jdbc.update(REMOVE_QUERY, filmId, userId);
    }
}
