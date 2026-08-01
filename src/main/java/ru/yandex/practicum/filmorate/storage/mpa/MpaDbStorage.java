package ru.yandex.practicum.filmorate.storage.mpa;

import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.BaseRepository;
import ru.yandex.practicum.filmorate.storage.mappers.MpaRowMapper;

@Repository
public class MpaDbStorage extends BaseRepository<Mpa> implements MpaStorage {
    private static final String FIND_ALL_QUERY =
            "SELECT mpa_id, name FROM mpa ORDER BY mpa_id";
    private static final String FIND_BY_ID_QUERY =
            "SELECT mpa_id, name FROM mpa WHERE mpa_id = ?";

    public MpaDbStorage(JdbcTemplate jdbc, MpaRowMapper mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<Mpa> getAll() {
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public Optional<Mpa> getById(int id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }
}
