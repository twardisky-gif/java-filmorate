package ru.yandex.practicum.filmorate.controller;

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
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.List;

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
    public Collection<FilmDto> getAll() {
        return FilmMapper.toDto(filmService.getAll());
    }

    @Override
    @GetMapping("/{id}")
    public FilmDto getById(@PathVariable long id) {
        return FilmMapper.toDto(filmService.getById(id));
    }

    @Override
    @PostMapping
    public FilmDto create(@Valid @RequestBody FilmDto film) {
        return FilmMapper.toDto(filmService.create(FilmMapper.toModel(film)));
    }

    @Override
    @PutMapping
    public FilmDto update(@Valid @RequestBody FilmDto film) {
        return FilmMapper.toDto(filmService.update(FilmMapper.toModel(film)));
    }

    @Override
    @DeleteMapping("/{id}")
    public void removeFilm(@PathVariable long id) {
        filmService.removeFilm(id);
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
    public List<FilmDto> getPopular(@RequestParam(defaultValue = "10") int count,
                                    @RequestParam(required = false) Integer genreId,
                                    @RequestParam(required = false) Integer year
    ) {
        return FilmMapper.toDto(filmService.getPopular(count, genreId, year));
    }

    @Override
    @GetMapping("/director/{directorId}")
    public List<FilmDto> getByDirector(@PathVariable long directorId,
                                       @RequestParam(defaultValue = "likes") String sortBy) {
        return FilmMapper.toDto(filmService.getByDirector(directorId, sortBy));
    }

    @Override
    @GetMapping("/search")
    public List<FilmDto> search(@RequestParam String query, @RequestParam String by) {
        return FilmMapper.toDto(filmService.search(query, by));
    }

    @Override
    @GetMapping("/common")
    public List<FilmDto> getCommonFilms(@RequestParam long userId, @RequestParam long friendId) {
        log.info("Получены общие фильмы для пользователей {} и {}", userId, friendId);
        return FilmMapper.toDto(filmService.getCommonFilms(userId, friendId));
    }
}
