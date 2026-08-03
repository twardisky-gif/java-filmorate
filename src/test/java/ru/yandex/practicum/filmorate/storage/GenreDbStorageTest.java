package ru.yandex.practicum.filmorate.storage;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.mappers.GenreRowMapper;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({GenreDbStorage.class, GenreRowMapper.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class GenreDbStorageTest {

    private static final int EXPECTED_GENRES_COUNT = 6;

    private final GenreDbStorage genreStorage;

    @Test
    void shouldFindAllGenres() {
        List<Genre> genres = genreStorage.getAll();

        assertThat(genres).hasSize(EXPECTED_GENRES_COUNT);
        assertThat(genres.get(0).getId()).isEqualTo(1);
        assertThat(genres.get(0).getName()).isEqualTo("Комедия");
    }

    @Test
    void shouldFindGenreById() {
        assertThat(genreStorage.getById(1))
                .isPresent()
                .hasValueSatisfying(genre -> {
                    assertThat(genre.getId()).isEqualTo(1);
                    assertThat(genre.getName()).isEqualTo("Комедия");
                });
    }

    @Test
    void shouldReturnEmptyForUnknownGenre() {
        assertThat(genreStorage.getById(9999)).isEmpty();
    }

    @Test
    void shouldFindGenresByIds() {
        List<Genre> genres = genreStorage.getByIds(List.of(3, 1));

        assertThat(genres)
                .extracting(Genre::getId)
                .containsExactly(1, 3);
    }

    @Test
    void shouldReturnEmptyListForEmptyIds() {
        assertThat(genreStorage.getByIds(List.of())).isEmpty();
    }
}
