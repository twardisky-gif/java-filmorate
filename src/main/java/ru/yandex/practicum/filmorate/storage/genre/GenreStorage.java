package ru.yandex.practicum.filmorate.storage.genre;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import ru.yandex.practicum.filmorate.model.Genre;

public interface GenreStorage {
    List<Genre> getAll();

    Optional<Genre> getById(int id);

    List<Genre> getByIds(Collection<Integer> ids);
}
