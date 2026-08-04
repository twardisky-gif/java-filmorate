package ru.yandex.practicum.filmorate.service;

import java.util.Collection;

import ru.yandex.practicum.filmorate.model.Director;

public interface DirectorService {
    Director create(Director director);

    Director update(Director director);

    Director getById(long id);

    Collection<Director> getAll();

    void delete(long id);
}
