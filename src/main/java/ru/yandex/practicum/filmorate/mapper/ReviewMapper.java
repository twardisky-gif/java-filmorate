package ru.yandex.practicum.filmorate.mapper;

import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;
import java.util.List;

public final class ReviewMapper {
    private ReviewMapper() {
    }

    public static ReviewDto toDto(Review review) {
        return new ReviewDto(review.getReviewId(), review.getContent(), review.getIsPositive(),
                review.getUserId(), review.getFilmId(), review.getUseful());
    }

    public static List<ReviewDto> toDto(Collection<Review> reviews) {
        return reviews.stream()
                .map(ReviewMapper::toDto)
                .toList();
    }

    public static Review toModel(ReviewDto reviewDto) {
        Review review = new Review();
        review.setReviewId(reviewDto.reviewId());
        review.setContent(reviewDto.content());
        review.setIsPositive(reviewDto.isPositive());
        review.setUserId(reviewDto.userId());
        review.setFilmId(reviewDto.filmId());
        review.setUseful(reviewDto.useful());
        return review;
    }
}
