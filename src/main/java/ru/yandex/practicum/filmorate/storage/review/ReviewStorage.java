package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewStorage {
    Review add(Review review);

    Review update(Review review);

    boolean delete(long id);

    Optional<Review> findById(long id);

    List<Review> findAll(Long filmId, int count);

    boolean existsByUserIdAndFilmId(long userId, long filmId);
}
