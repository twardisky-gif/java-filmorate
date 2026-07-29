package ru.yandex.practicum.filmorate.controller;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {
    private FilmController controller;

    @BeforeEach
    void setUp() {
        controller = new FilmController();
    }

    private Film createValidFilm() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        return film;
    }

    @Test
    void shouldCreateValidFilm() {
        Film film = createValidFilm();
        Film created = controller.create(film);

        assertEquals(1, created.getId());
        assertEquals("Test Film", created.getName());
    }

    @Test
    void shouldFailWhenNameIsEmpty() {
        Film film = createValidFilm();
        film.setName("");

        assertThrows(ValidationException.class, () -> controller.create(film));
    }

    @Test
    void shouldFailWhenNameIsBlank() {
        Film film = createValidFilm();
        film.setName("   ");

        assertThrows(ValidationException.class, () -> controller.create(film));
    }

    @Test
    void shouldFailWhenNameIsNull() {
        Film film = createValidFilm();
        film.setName(null);

        assertThrows(ValidationException.class, () -> controller.create(film));
    }

    @Test
    void shouldFailWhenDescriptionIsTooLong() {
        Film film = createValidFilm();
        film.setDescription("a".repeat(201));

        assertThrows(ValidationException.class, () -> controller.create(film));
    }

    @Test
    void shouldAcceptDescriptionOf200Chars() {
        Film film = createValidFilm();
        film.setDescription("a".repeat(200));

        Film created = controller.create(film);
        assertEquals(200, created.getDescription().length());
    }

    @Test
    void shouldFailWhenReleaseDateIsTooEarly() {
        Film film = createValidFilm();
        film.setReleaseDate(LocalDate.of(1895, 12, 27));

        assertThrows(ValidationException.class, () -> controller.create(film));
    }

    @Test
    void shouldAcceptReleaseDateOnCinemaBirthday() {
        Film film = createValidFilm();
        film.setReleaseDate(LocalDate.of(1895, 12, 28));

        Film created = controller.create(film);
        assertNotNull(created);
    }

    @Test
    void shouldFailWhenDurationIsZero() {
        Film film = createValidFilm();
        film.setDuration(0);

        assertThrows(ValidationException.class, () -> controller.create(film));
    }

    @Test
    void shouldFailWhenDurationIsNegative() {
        Film film = createValidFilm();
        film.setDuration(-1);

        assertThrows(ValidationException.class, () -> controller.create(film));
    }

    @Test
    void shouldGetAllFilms() {
        Film film1 = createValidFilm();
        Film film2 = createValidFilm();
        film2.setName("Another Film");

        controller.create(film1);
        controller.create(film2);

        assertEquals(2, controller.getAll().size());
    }

    @Test
    void shouldUpdateFilm() {
        Film film = createValidFilm();
        Film created = controller.create(film);

        created.setName("Updated Name");
        Film updated = controller.update(created);

        assertEquals("Updated Name", updated.getName());
    }

    @Test
    void shouldFailUpdateWithUnknownId() {
        Film film = createValidFilm();
        film.setId(999);

        assertThrows(NotFoundException.class, () -> controller.update(film));
    }
}
