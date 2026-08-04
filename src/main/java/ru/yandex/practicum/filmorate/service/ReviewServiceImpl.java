package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.EventOperation;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.event.EventStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewReactionStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;

import java.util.List;

@Slf4j
@Service
public class ReviewServiceImpl implements ReviewService {
    private static final int DEFAULT_REVIEWS_COUNT = 10;

    private final ReviewStorage reviewStorage;
    private final ReviewReactionStorage reviewReactionStorage;
    private final UserService userService;
    private final FilmService filmService;
    private final EventStorage eventStorage;

    public ReviewServiceImpl(ReviewStorage reviewStorage,
                             ReviewReactionStorage reviewReactionStorage,
                             UserService userService,
                             FilmService filmService,
                             EventStorage eventStorage) {
        this.reviewStorage = reviewStorage;
        this.reviewReactionStorage = reviewReactionStorage;
        this.userService = userService;
        this.filmService = filmService;
        this.eventStorage = eventStorage;
    }

    @Override
    public Review create(Review review) {
        userService.getById(review.getUserId());
        filmService.getById(review.getFilmId());

        if (reviewStorage.existsByUserIdAndFilmId(review.getUserId(), review.getFilmId())) {
            throw new ValidationException("Отзыв этого пользователя на фильм уже существует");
        }

        Review created = reviewStorage.add(review);
        eventStorage.add(created.getUserId(), EventType.REVIEW, EventOperation.ADD, created.getReviewId());

        log.info("Добавлен отзыв с id={} пользователя {} на фильм {}",
                created.getReviewId(), created.getUserId(), created.getFilmId());
        return created;
    }

    @Override
    public Review update(Review review) {
        Review existing = getReviewOrThrow(review.getReviewId());
        existing.setContent(review.getContent());
        existing.setIsPositive(review.getIsPositive());

        reviewStorage.update(existing);
        eventStorage.add(existing.getUserId(), EventType.REVIEW, EventOperation.UPDATE, existing.getReviewId());

        log.info("Обновлен отзыв с id={}", existing.getReviewId());
        return getById(existing.getReviewId());
    }

    @Override
    public void delete(long id) {
        Review existing = getReviewOrThrow(id);
        reviewStorage.delete(id);
        eventStorage.add(existing.getUserId(), EventType.REVIEW, EventOperation.REMOVE, id);
        log.info("Удален отзыв с id={}", id);
    }

    @Override
    public Review getById(long id) {
        return getReviewOrThrow(id);
    }

    @Override
    public List<Review> getReviews(Long filmId, int count) {

        if (filmId != null) {
            filmService.getById(filmId);
        }

        int limit = count > 0 ? count : DEFAULT_REVIEWS_COUNT;
        return reviewStorage.findAll(filmId, limit);
    }

    @Override
    public void addLike(long reviewId, long userId) {
        getReviewOrThrow(reviewId);
        userService.getById(userId);
        reviewReactionStorage.addLike(reviewId, userId);
        log.info("Пользователь {} поставил лайк отзыву {}", userId, reviewId);
    }

    @Override
    public void addDislike(long reviewId, long userId) {
        getReviewOrThrow(reviewId);
        userService.getById(userId);
        reviewReactionStorage.addDislike(reviewId, userId);
        log.info("Пользователь {} поставил дизлайк отзыву {}", userId, reviewId);
    }

    @Override
    public void removeLike(long reviewId, long userId) {
        getReviewOrThrow(reviewId);
        userService.getById(userId);
        reviewReactionStorage.removeLike(reviewId, userId);
        log.info("Пользователь {} удалил лайк отзыва {}", userId, reviewId);
    }

    @Override
    public void removeDislike(long reviewId, long userId) {
        getReviewOrThrow(reviewId);
        userService.getById(userId);
        reviewReactionStorage.removeDislike(reviewId, userId);
        log.info("Пользователь {} удалил дизлайк отзыва {}", userId, reviewId);
    }

    private Review getReviewOrThrow(long id) {
        return reviewStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Отзыв с id=" + id + " не найден"));
    }
}
