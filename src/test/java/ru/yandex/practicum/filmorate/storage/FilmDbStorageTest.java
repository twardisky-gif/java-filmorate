package ru.yandex.practicum.filmorate.storage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.like.LikeDbStorage;
import ru.yandex.practicum.filmorate.storage.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({FilmDbStorage.class, FilmRowMapper.class, LikeDbStorage.class, UserDbStorage.class, UserRowMapper.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmDbStorageTest {

    private static final int MPA_PG13 = 3;

    private final FilmDbStorage filmStorage;
    private final LikeDbStorage likeStorage;
    private final UserDbStorage userStorage;

    private Film newFilm(String name, Set<Genre> genres) {
        Film film = new Film();
        film.setName(name);
        film.setDescription("Описание " + name);
        film.setReleaseDate(LocalDate.of(1999, 4, 30));
        film.setDuration(120);
        film.setMpa(new Mpa(MPA_PG13, null));
        film.setGenres(genres == null ? new LinkedHashSet<>() : genres);
        return film;
    }

    private User newUser(String login) {
        User user = new User();
        user.setEmail(login + "@mail.ru");
        user.setLogin(login);
        user.setName("Имя " + login);
        user.setBirthday(LocalDate.of(1990, 5, 15));
        return userStorage.add(user);
    }

    @Test
    void shouldCreateFilm() {
        Film created = filmStorage.add(newFilm("Новый фильм", null));

        assertThat(created.getId()).isPositive();
        assertThat(created.getName()).isEqualTo("Новый фильм");
    }

    @Test
    void shouldFindFilmById() {
        Film created = filmStorage.add(newFilm("Поиск по id", null));

        assertThat(filmStorage.getById(created.getId()))
                .isPresent()
                .hasValueSatisfying(film -> {
                    assertThat(film).hasFieldOrPropertyWithValue("id", created.getId());
                    assertThat(film.getName()).isEqualTo("Поиск по id");
                    assertThat(film.getMpa().getId()).isEqualTo(MPA_PG13);
                    assertThat(film.getMpa().getName()).isEqualTo("PG-13");
                });
    }

    @Test
    void shouldReturnEmptyForUnknownId() {
        assertThat(filmStorage.getById(9999L)).isEmpty();
    }

    @Test
    void shouldUpdateFilm() {
        Film created = filmStorage.add(newFilm("До обновления", null));
        created.setName("После обновления");
        created.setDuration(190);

        filmStorage.update(created);

        assertThat(filmStorage.getById(created.getId()))
                .isPresent()
                .hasValueSatisfying(film -> {
                    assertThat(film.getName()).isEqualTo("После обновления");
                    assertThat(film.getDuration()).isEqualTo(190);
                });
    }

    @Test
    void shouldFindAllFilms() {
        filmStorage.add(newFilm("Первый", null));
        filmStorage.add(newFilm("Второй", null));

        Collection<Film> films = filmStorage.getAll();

        assertThat(films).hasSize(2);
        assertThat(films).extracting(Film::getName).containsExactlyInAnyOrder("Первый", "Второй");
    }

    @Test
    void shouldDeleteFilm() {
        Film created = filmStorage.add(newFilm("На удаление", null));

        filmStorage.delete(created.getId());

        assertThat(filmStorage.getById(created.getId())).isEmpty();
    }

    @Test
    void shouldSaveAndLoadGenres() {
        Set<Genre> genres = new LinkedHashSet<>(List.of(new Genre(2, null), new Genre(1, null)));

        Film created = filmStorage.add(newFilm("С жанрами", genres));

        assertThat(filmStorage.getById(created.getId()))
                .isPresent()
                .hasValueSatisfying(film -> assertThat(film.getGenres())
                        .extracting(Genre::getId)
                        .containsExactly(1, 2));
    }

    @Test
    void shouldReplaceGenresOnUpdate() {
        Film created = filmStorage.add(newFilm("Смена жанров", new LinkedHashSet<>(List.of(new Genre(1, null)))));
        created.setGenres(new LinkedHashSet<>(List.of(new Genre(4, null))));

        filmStorage.update(created);

        assertThat(filmStorage.getById(created.getId()))
                .isPresent()
                .hasValueSatisfying(film -> assertThat(film.getGenres())
                        .extracting(Genre::getId)
                        .containsExactly(4));
    }

    @Test
    void shouldAddLike() {
        Film film = filmStorage.add(newFilm("С лайком", null));
        User user = newUser("liker");

        likeStorage.add(film.getId(), user.getId());

        assertThat(filmStorage.getPopular(1))
                .extracting(Film::getId)
                .containsExactly(film.getId());
    }

    @Test
    void shouldRemoveLike() {
        Film liked = filmStorage.add(newFilm("Был лайк", null));
        Film other = filmStorage.add(newFilm("Без лайка", null));
        User user = newUser("liker");
        likeStorage.add(liked.getId(), user.getId());

        likeStorage.remove(liked.getId(), user.getId());

        assertThat(filmStorage.getPopular(2))
                .extracting(Film::getId)
                .containsExactly(liked.getId(), other.getId());
    }

    @Test
    void shouldSortPopularFilmsByLikes() {
        Film unpopular = filmStorage.add(newFilm("Непопулярный", null));
        Film popular = filmStorage.add(newFilm("Популярный", null));
        User first = newUser("first");
        User second = newUser("second");
        likeStorage.add(popular.getId(), first.getId());
        likeStorage.add(popular.getId(), second.getId());
        likeStorage.add(unpopular.getId(), first.getId());

        List<Film> films = filmStorage.getPopular(2);

        assertThat(films)
                .extracting(Film::getName)
                .containsExactly("Популярный", "Непопулярный");
    }

    @Test
    void shouldLimitPopularFilms() {
        filmStorage.add(newFilm("Первый", null));
        filmStorage.add(newFilm("Второй", null));

        assertThat(filmStorage.getPopular(1)).hasSize(1);
    }
}
