package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface DirectorStorage {
    Director add(Director director);

    Director update(Director director);

    void delete(long id);

    Optional<Director> getById(long id);

    Collection<Director> getAll();

    List<Director> getByIds(List<Long> ids);
}
