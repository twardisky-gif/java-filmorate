package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.mapper.MpaMapper;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/mpa")
public class MpaControllerImpl implements MpaController {
    private final MpaService mpaService;

    public MpaControllerImpl(MpaService mpaService) {
        this.mpaService = mpaService;
    }

    @Override
    @GetMapping
    public List<MpaDto> getAll() {
        log.info("Запрошен список рейтингов");
        return MpaMapper.toDto(mpaService.getAll());
    }

    @Override
    @GetMapping("/{id}")
    public MpaDto getById(@PathVariable int id) {
        log.info("Запрошен рейтинг с id={}", id);
        return MpaMapper.toDto(mpaService.getById(id));
    }
}
