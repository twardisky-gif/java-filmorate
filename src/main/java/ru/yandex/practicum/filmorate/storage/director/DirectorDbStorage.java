package ru.yandex.practicum.filmorate.storage.director;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.BaseRepository;
import ru.yandex.practicum.filmorate.storage.mappers.DirectorRowMapper;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
public class DirectorDbStorage extends BaseRepository<Director> implements DirectorStorage {
    private static final String INSERT_QUERY = "INSERT INTO directors (name) VALUES (?)";
    private static final String UPDATE_QUERY = "UPDATE directors SET name = ? WHERE director_id = ?";
    private static final String DELETE_QUERY = "DELETE FROM directors WHERE director_id = ?";
    private static final String FIND_BY_ID_QUERY =
            "SELECT director_id, name FROM directors WHERE director_id = ?";
    private static final String FIND_ALL_QUERY =
            "SELECT director_id, name FROM directors ORDER BY director_id";

    public DirectorDbStorage(JdbcTemplate jdbc, DirectorRowMapper mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Director add(Director director) {
        director.setId(insert(INSERT_QUERY, director.getName()));
        return director;
    }

    @Override
    public Director update(Director director) {
        update(UPDATE_QUERY, director.getName(), director.getId());
        return director;
    }

    @Override
    public void delete(long id) {
        delete(DELETE_QUERY, id);
    }

    @Override
    public Optional<Director> getById(long id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }

    @Override
    public Collection<Director> getAll() {
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public List<Director> getByIds(List<Long> ids) {
        if (ids.isEmpty()) {
            return List.of();
        }
        String placeholders = String.join(",", Collections.nCopies(ids.size(), "?"));
        String query = "SELECT director_id, name FROM directors WHERE director_id IN (%s) ORDER BY director_id"
                .formatted(placeholders);
        return findMany(query, ids.toArray());
    }
}
