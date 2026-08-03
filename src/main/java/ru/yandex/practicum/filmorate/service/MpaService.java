package ru.yandex.practicum.filmorate.service;

import java.util.List;

import ru.yandex.practicum.filmorate.model.Mpa;

public interface MpaService {
    List<Mpa> getAll();

    Mpa getById(int id);
}
