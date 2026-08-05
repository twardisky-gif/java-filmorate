package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;

import java.util.Collection;

@Service
public class DirectorServiceImpl implements DirectorService {
    private final DirectorStorage directorStorage;

    public DirectorServiceImpl(DirectorStorage directorStorage) {
        this.directorStorage = directorStorage;
    }

    @Override
    public Director create(Director director) {
        return directorStorage.add(director);
    }

    @Override
    public Director update(Director director) {
        getById(director.getId());
        return directorStorage.update(director);
    }

    @Override
    public Director getById(long id) {
        return directorStorage.getById(id)
                .orElseThrow(() -> new NotFoundException("Режиссёр с id=" + id + " не найден"));
    }

    @Override
    public Collection<Director> getAll() {
        return directorStorage.getAll();
    }

    @Override
    public void delete(long id) {
        getById(id);
        directorStorage.delete(id);
    }
}
