package ru.yandex.practicum.filmorate.controller;

import java.util.Collection;
import java.util.List;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmControllerImpl implements FilmController {
    private final FilmService filmService;

    public FilmControllerImpl(FilmService filmService) {
        this.filmService = filmService;
    }

    @Override
    @GetMapping
    public Collection<Film> getAll() {
        return filmService.getAll();
    }

    @Override
    @GetMapping("/{id}")
    public Film getById(@PathVariable long id) {
        return filmService.getById(id);
    }

    @Override
    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        return filmService.create(film);
    }

    @Override
    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        return filmService.update(film);
    }

    @Override
    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable long id, @PathVariable long userId) {
        filmService.addLike(id, userId);
    }

    @Override
    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable long id, @PathVariable long userId) {
        filmService.removeLike(id, userId);
    }

    @Override
    @GetMapping("/popular")
    public List<Film> getPopular(@RequestParam(defaultValue = "10") int count) {
        return filmService.getPopular(count);
    }
    @Override
    @GetMapping("/{userId}/recommendations")
    public List<Film> getRecommendations(@PathVariable long userId,
                                         @RequestParam(defaultValue = "10") int count) {
        return filmService.getRecommendations(userId, count);
    }
}
