package ru.yandex.practicum.filmorate.storage;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mappers.MpaRowMapper;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({MpaDbStorage.class, MpaRowMapper.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class MpaDbStorageTest {

    private static final int EXPECTED_MPA_COUNT = 5;

    private final MpaDbStorage mpaStorage;

    @Test
    void shouldFindAllMpa() {
        List<Mpa> ratings = mpaStorage.getAll();

        assertThat(ratings).hasSize(EXPECTED_MPA_COUNT);
        assertThat(ratings)
                .extracting(Mpa::getName)
                .containsExactly("G", "PG", "PG-13", "R", "NC-17");
    }

    @Test
    void shouldFindMpaById() {
        assertThat(mpaStorage.getById(1))
                .isPresent()
                .hasValueSatisfying(mpa -> {
                    assertThat(mpa.getId()).isEqualTo(1);
                    assertThat(mpa.getName()).isEqualTo("G");
                });
    }

    @Test
    void shouldReturnEmptyForUnknownMpa() {
        assertThat(mpaStorage.getById(9999)).isEmpty();
    }
}
