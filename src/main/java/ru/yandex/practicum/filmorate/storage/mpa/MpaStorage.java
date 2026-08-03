package ru.yandex.practicum.filmorate.storage.mpa;

import java.util.List;
import java.util.Optional;

import ru.yandex.practicum.filmorate.model.Mpa;

public interface MpaStorage {
    List<Mpa> getAll();

    Optional<Mpa> getById(int id);
}
