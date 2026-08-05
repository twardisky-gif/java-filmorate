package ru.yandex.practicum.filmorate.storage.like;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class LikeDbStorage implements LikeStorage {
    private static final String ADD_QUERY =
            "INSERT INTO likes (film_id, user_id) "
                    + "SELECT ?, ? WHERE NOT EXISTS ("
                    + "SELECT 1 FROM likes WHERE film_id = ? AND user_id = ?)";
    private static final String REMOVE_QUERY =
            "DELETE FROM likes WHERE film_id = ? AND user_id = ?";

    private final JdbcTemplate jdbc;

    public LikeDbStorage(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public boolean add(long filmId, long userId) {
        return jdbc.update(ADD_QUERY, filmId, userId, filmId, userId) > 0;
    }

    @Override
    public boolean remove(long filmId, long userId) {
        return jdbc.update(REMOVE_QUERY, filmId, userId) > 0;
    }
}
