package ru.yandex.practicum.filmorate.service;

import java.time.LocalDate;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.like.LikeStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

@Slf4j
@Service
public class FilmServiceImpl implements FilmService {
    private static final LocalDate CINEMA_BIRTHDAY = LocalDate.of(1895, 12, 28);
    private static final int DEFAULT_POPULAR_COUNT = 10;

    private final FilmStorage filmStorage;
    private final LikeStorage likeStorage;
    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;
    private final UserService userService;

    public FilmServiceImpl(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                           LikeStorage likeStorage,
                           GenreStorage genreStorage,
                           MpaStorage mpaStorage,
                           UserService userService) {
        this.filmStorage = filmStorage;
        this.likeStorage = likeStorage;
        this.genreStorage = genreStorage;
        this.mpaStorage = mpaStorage;
        this.userService = userService;
    }

    @Override
    public Film create(Film film) {
        validateReleaseDate(film);
        applyMpa(film);
        applyGenres(film);
        Film created = filmStorage.add(film);
        log.info("Добавлен фильм: {}", created.getName());
        return created;
    }

    @Override
    public Film update(Film film) {
        getFilmOrThrow(film.getId());
        validateReleaseDate(film);
        applyMpa(film);
        applyGenres(film);
        Film updated = filmStorage.update(film);
        log.info("Обновлен фильм: {}", updated.getName());
        return updated;
    }

    @Override
    public void removeFilm(Long id) {
        boolean isRemove = filmStorage.delete(id);
        if (!isRemove) {
            throw new NotFoundException("Фильм с id=" + id + " не найден");
        }
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
        getFilmOrThrow(filmId);
        userService.getById(userId);
        likeStorage.add(filmId, userId);
        log.info("Пользователь {} поставил лайк фильму {}", userId, filmId);
    }

    @Override
    public void removeLike(long filmId, long userId) {
        getFilmOrThrow(filmId);
        userService.getById(userId);
        likeStorage.remove(filmId, userId);
        log.info("Пользователь {} удалил лайк фильму {}", userId, filmId);
    }

    @Override
    public List<Film> getPopular(int count) {
        int limit = count > 0 ? count : DEFAULT_POPULAR_COUNT;
        return filmStorage.getPopular(limit);
    }

    private void validateReleaseDate(Film film) {
        LocalDate releaseDate = film.getReleaseDate();
        if (releaseDate == null || releaseDate.isBefore(CINEMA_BIRTHDAY)) {
            log.warn("Дата релиза раньше 28 декабря 1895 года");
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
    }

    private void applyMpa(Film film) {
        int mpaId = film.getMpa().getId();
        Mpa mpa = mpaStorage.getById(mpaId)
                .orElseThrow(() -> new NotFoundException("Рейтинг с id=" + mpaId + " не найден"));
        film.setMpa(mpa);
    }

    private void applyGenres(Film film) {
        if (film.getGenres() == null || film.getGenres().isEmpty()) {
            film.setGenres(new LinkedHashSet<>());
            return;
        }
        List<Integer> genreIds = film.getGenres().stream()
                .map(Genre::getId)
                .distinct()
                .sorted()
                .toList();
        List<Genre> genres = genreStorage.getByIds(genreIds);
        if (genres.size() != genreIds.size()) {
            throw new NotFoundException("Указан несуществующий жанр");
        }
        film.setGenres(new LinkedHashSet<>(genres));
    }

    private Film getFilmOrThrow(long id) {
        return filmStorage.getById(id)
                .orElseThrow(() -> new NotFoundException("Фильм с id=" + id + " не найден"));
    }
}
