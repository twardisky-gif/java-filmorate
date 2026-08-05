package ru.yandex.practicum.filmorate.storage.review;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.BaseRepository;
import ru.yandex.practicum.filmorate.storage.mappers.ReviewRowMapper;

import java.util.List;
import java.util.Optional;

@Repository
public class ReviewDbStorage extends BaseRepository<Review> implements ReviewStorage {

    private static final String SELECT_REVIEW_COLUMNS =
            "SELECT r.review_id, r.content, r.is_positive, r.user_id, r.film_id, "
                    + "COALESCE(SUM(CASE WHEN rr.user_id IS NULL THEN 0 "
                    + "WHEN rr.is_like THEN 1 ELSE -1 END), 0) AS useful "
                    + "FROM reviews r "
                    + "LEFT JOIN review_reactions rr ON rr.review_id = r.review_id ";

    private static final String GROUP_BY =
            "GROUP BY r.review_id, r.content, r.is_positive, r.user_id, r.film_id ";

    private static final String FIND_BY_ID_QUERY =
            SELECT_REVIEW_COLUMNS + "WHERE r.review_id = ? " + GROUP_BY;

    private static final String FIND_ALL_QUERY =
            SELECT_REVIEW_COLUMNS + GROUP_BY + "ORDER BY useful DESC, r.review_id LIMIT ?";

    private static final String FIND_BY_FILM_ID_QUERY =
            SELECT_REVIEW_COLUMNS + "WHERE r.film_id = ? " + GROUP_BY
                    + "ORDER BY useful DESC, r.review_id LIMIT ?";

    private static final String INSERT_QUERY =
            "INSERT INTO reviews (content, is_positive, user_id, film_id) VALUES (?, ?, ?, ?)";

    private static final String UPDATE_QUERY =
            "UPDATE reviews SET content = ?, is_positive = ? WHERE review_id = ?";

    private static final String DELETE_QUERY =
            "DELETE FROM reviews WHERE review_id = ?";

    private static final String EXISTS_BY_USER_AND_FILM_QUERY =
            "SELECT COUNT(*) FROM reviews WHERE user_id = ? AND film_id = ?";

    public ReviewDbStorage(JdbcTemplate jdbc, ReviewRowMapper mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Review add(Review review) {
        long id = insert(INSERT_QUERY,
                review.getContent(),
                review.getIsPositive(),
                review.getUserId(),
                review.getFilmId());
        review.setReviewId(id);
        review.setUseful(0);
        return review;
    }

    @Override
    public Review update(Review review) {
        update(UPDATE_QUERY,
                review.getContent(),
                review.getIsPositive(),
                review.getReviewId());
        return review;
    }

    @Override
    public boolean delete(long id) {
        return delete(DELETE_QUERY, id);
    }

    @Override
    public Optional<Review> findById(long id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }

    @Override
    public List<Review> findAll(Long filmId, int count) {
        if (filmId != null) {
            return findMany(FIND_BY_FILM_ID_QUERY, filmId, count);
        }
        return findMany(FIND_ALL_QUERY, count);
    }

    @Override
    public boolean existsByUserIdAndFilmId(long userId, long filmId) {
        Integer count = jdbc.queryForObject(EXISTS_BY_USER_AND_FILM_QUERY, Integer.class, userId, filmId);
        return count != null && count > 0;
    }
}
