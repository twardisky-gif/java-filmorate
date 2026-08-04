package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.List;

@Service
public class MpaServiceImpl implements MpaService {
    private final MpaStorage mpaStorage;

    public MpaServiceImpl(MpaStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    @Override
    public List<Mpa> getAll() {
        return mpaStorage.getAll();
    }

    @Override
    public Mpa getById(int id) {
        return mpaStorage.getById(id)
                .orElseThrow(() -> new NotFoundException("Рейтинг с id=" + id + " не найден"));
    }
}
