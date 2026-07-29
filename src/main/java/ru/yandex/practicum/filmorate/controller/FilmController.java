package ru.yandex.practicum.filmorate.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private static final LocalDate CINEMA_BIRTHDAY = LocalDate.of(1895, 12, 28);
    private static final int MAX_DESCRIPTION_LENGTH = 200;
    private final Map<Integer, Film> films = new HashMap<>();
    private int nextId = 1;

    @GetMapping
    public Collection<Film> getAll() {
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        validate(film);
        film.setId(nextId++);
        films.put(film.getId(), film);
        log.info("Добавлен фильм: {}", film.getName());
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film film) {
        int id = film.getId();
        if (!films.containsKey(id)) {
            throw new ValidationException("Фильм с id=" + id + " не найден");
        }
        validate(film);
        films.put(id, film);
        log.info("Обновлен фильм: {}", film.getName());
        return film;
    }

    private void validate(Film film) {
        String name = film.getName();
        if (name == null || name.isBlank()) {
            log.warn("Название фильма пустое");
            throw new ValidationException("Название не может быть пустым");
        }
        String description = film.getDescription();
        if (description != null && description.length() > MAX_DESCRIPTION_LENGTH) {
            log.warn("Описание фильма превышает {} символов", MAX_DESCRIPTION_LENGTH);
            throw new ValidationException("Максимальная длина описания " + MAX_DESCRIPTION_LENGTH + " символов");
        }
        LocalDate releaseDate = film.getReleaseDate();
        if (releaseDate != null && releaseDate.isBefore(CINEMA_BIRTHDAY)) {
            log.warn("Дата релиза раньше 28 декабря 1895 года");
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
        if (film.getDuration() <= 0) {
            log.warn("Продолжительность фильма отрицательная или равна нулю");
            throw new ValidationException("Продолжительность фильма должна быть положительным числом");
        }
    }
}
