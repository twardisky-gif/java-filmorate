package ru.yandex.practicum.filmorate.controller;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.service.GenreService;

@Slf4j
@RestController
@RequestMapping("/genres")
public class GenreControllerImpl implements GenreController {
    private final GenreService genreService;

    public GenreControllerImpl(GenreService genreService) {
        this.genreService = genreService;
    }

    @Override
    @GetMapping
    public List<GenreDto> getAll() {
        log.info("Запрошен список жанров");
        return GenreMapper.toDto(genreService.getAll());
    }

    @Override
    @GetMapping("/{id}")
    public GenreDto getById(@PathVariable int id) {
        log.info("Запрошен жанр с id={}", id);
        return GenreMapper.toDto(genreService.getById(id));
    }
}
