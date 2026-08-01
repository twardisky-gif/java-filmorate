package ru.yandex.practicum.filmorate.service;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

@Slf4j
@Service
public class FilmServiceImpl implements FilmService {
    private static final LocalDate CINEMA_BIRTHDAY = LocalDate.of(1895, 12, 28);
    private static final int DEFAULT_POPULAR_COUNT = 10;
    private final FilmStorage filmStorage;
    private final UserService userService;

    public FilmServiceImpl(FilmStorage filmStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
    }

    @Override
    public Film create(Film film) {
        validateReleaseDate(film);
        Film created = filmStorage.add(film);
        log.info("Добавлен фильм: {}", created.getName());
        return created;
    }

    @Override
    public Film update(Film film) {
        getFilmOrThrow(film.getId());
        validateReleaseDate(film);
        Film updated = filmStorage.update(film);
        log.info("Обновлен фильм: {}", updated.getName());
        return updated;
    }

    @Override
    public Film getById(long id) {
        return getFilmOrThrow(id);
    }

    @Override
    public Collection<Film> getAll() {
        return filmStorage.getAll();
    }

    @Override
    public void addLike(long filmId, long userId) {
        Film film = getFilmOrThrow(filmId);
        userService.getById(userId);
        film.getLikes().add(userId);
        log.info("Пользователь {} поставил лайк фильму {}", userId, filmId);
    }

    @Override
    public void removeLike(long filmId, long userId) {
        Film film = getFilmOrThrow(filmId);
        userService.getById(userId);
        film.getLikes().remove(userId);
        log.info("Пользователь {} удалил лайк фильму {}", userId, filmId);
    }

    @Override
    public List<Film> getPopular(int count) {
        int limit = count > 0 ? count : DEFAULT_POPULAR_COUNT;
        return filmStorage.getAll().stream()
                .sorted((f1, f2) -> Integer.compare(f2.getLikes().size(), f1.getLikes().size()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    private void validateReleaseDate(Film film) {
        LocalDate releaseDate = film.getReleaseDate();
        if (releaseDate != null && releaseDate.isBefore(CINEMA_BIRTHDAY)) {
            log.warn("Дата релиза раньше 28 декабря 1895 года");
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
    }

    private Film getFilmOrThrow(long id) {
        return filmStorage.getById(id)
                .orElseThrow(() -> new NotFoundException("Фильм с id=" + id + " не найден"));
    }
}
