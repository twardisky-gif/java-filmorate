package ru.yandex.practicum.filmorate.storage.review;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ReviewReactionDbStorage implements ReviewReactionStorage {

    private static final String MERGE_QUERY =
            "MERGE INTO review_reactions (review_id, user_id, is_like) KEY (review_id, user_id) VALUES (?, ?, ?)";

    private static final String REMOVE_LIKE_QUERY =
            "DELETE FROM review_reactions WHERE review_id = ? AND user_id = ? AND is_like = TRUE";

    private static final String REMOVE_DISLIKE_QUERY =
            "DELETE FROM review_reactions WHERE review_id = ? AND user_id = ? AND is_like = FALSE";

    private final JdbcTemplate jdbc;

    public ReviewReactionDbStorage(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public void addLike(long reviewId, long userId) {
        jdbc.update(MERGE_QUERY, reviewId, userId, true);
    }

    @Override
    public void addDislike(long reviewId, long userId) {
        jdbc.update(MERGE_QUERY, reviewId, userId, false);
    }

    @Override
    public void removeLike(long reviewId, long userId) {
        jdbc.update(REMOVE_LIKE_QUERY, reviewId, userId);
    }

    @Override
    public void removeDislike(long reviewId, long userId) {
        jdbc.update(REMOVE_DISLIKE_QUERY, reviewId, userId);
    }
}
