package ru.yandex.practicum.filmorate.controller;

import java.util.Collection;

import ru.yandex.practicum.filmorate.model.Director;

public interface DirectorController {
    Collection<Director> getAll();

    Director getById(long id);

    Director create(Director director);

    Director update(Director director);

    void delete(long id);
}
