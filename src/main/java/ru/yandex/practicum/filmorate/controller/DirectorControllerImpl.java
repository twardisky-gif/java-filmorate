package ru.yandex.practicum.filmorate.controller;

import java.util.Collection;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

@RestController
@RequestMapping("/directors")
public class DirectorControllerImpl implements DirectorController {
    private final DirectorService directorService;

    public DirectorControllerImpl(DirectorService directorService) {
        this.directorService = directorService;
    }

    @Override
    @GetMapping
    public Collection<Director> getAll() {
        return directorService.getAll();
    }

    @Override
    @GetMapping("/{id}")
    public Director getById(@PathVariable long id) {
        return directorService.getById(id);
    }

    @Override
    @PostMapping
    public Director create(@Valid @RequestBody Director director) {
        return directorService.create(director);
    }

    @Override
    @PutMapping
    public Director update(@Valid @RequestBody Director director) {
        return directorService.update(director);
    }

    @Override
    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        directorService.delete(id);
    }
}
