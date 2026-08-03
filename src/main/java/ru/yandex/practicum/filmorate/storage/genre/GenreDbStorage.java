package ru.yandex.practicum.filmorate.storage.genre;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.BaseRepository;
import ru.yandex.practicum.filmorate.storage.mappers.GenreRowMapper;

@Repository
public class GenreDbStorage extends BaseRepository<Genre> implements GenreStorage {
    private static final String FIND_ALL_QUERY =
            "SELECT genre_id, name FROM genres ORDER BY genre_id";
    private static final String FIND_BY_ID_QUERY =
            "SELECT genre_id, name FROM genres WHERE genre_id = ?";
    private static final String FIND_BY_IDS_QUERY =
            "SELECT genre_id, name FROM genres WHERE genre_id IN (%s) ORDER BY genre_id";

    public GenreDbStorage(JdbcTemplate jdbc, GenreRowMapper mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<Genre> getAll() {
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public Optional<Genre> getById(int id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }

    @Override
    public List<Genre> getByIds(Collection<Integer> ids) {
        if (ids.isEmpty()) {
            return List.of();
        }
        String placeholders = String.join(",", Collections.nCopies(ids.size(), "?"));
        return findMany(FIND_BY_IDS_QUERY.formatted(placeholders), ids.toArray());
    }
}
