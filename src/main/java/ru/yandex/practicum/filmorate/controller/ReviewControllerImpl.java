package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.mapper.ReviewMapper;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.List;

@RestController
@RequestMapping("/reviews")
public class ReviewControllerImpl implements ReviewController {
    private final ReviewService reviewService;

    public ReviewControllerImpl(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @Override
    @PostMapping
    public ReviewDto create(@Valid @RequestBody ReviewDto review) {
        return ReviewMapper.toDto(reviewService.create(ReviewMapper.toModel(review)));
    }

    @Override
    @PutMapping
    public ReviewDto update(@Valid @RequestBody ReviewDto review) {
        return ReviewMapper.toDto(reviewService.update(ReviewMapper.toModel(review)));
    }

    @Override
    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        reviewService.delete(id);
    }

    @Override
    @GetMapping("/{id}")
    public ReviewDto getById(@PathVariable long id) {
        return ReviewMapper.toDto(reviewService.getById(id));
    }

    @Override
    @GetMapping
    public List<ReviewDto> getReviews(@RequestParam(required = false) Long filmId,
                                      @RequestParam(defaultValue = "10") int count) {
        return ReviewMapper.toDto(reviewService.getReviews(filmId, count));
    }

    @Override
    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable long id, @PathVariable long userId) {
        reviewService.addLike(id, userId);
    }

    @Override
    @PutMapping("/{id}/dislike/{userId}")
    public void addDislike(@PathVariable long id, @PathVariable long userId) {
        reviewService.addDislike(id, userId);
    }

    @Override
    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable long id, @PathVariable long userId) {
        reviewService.removeLike(id, userId);
    }

    @Override
    @DeleteMapping("/{id}/dislike/{userId}")
    public void removeDislike(@PathVariable long id, @PathVariable long userId) {
        reviewService.removeDislike(id, userId);
    }
}
