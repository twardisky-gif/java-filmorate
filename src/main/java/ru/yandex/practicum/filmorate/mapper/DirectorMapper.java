package ru.yandex.practicum.filmorate.mapper;

import java.util.Collection;
import java.util.List;

import ru.yandex.practicum.filmorate.dto.DirectorDto;
import ru.yandex.practicum.filmorate.model.Director;

public final class DirectorMapper {

    private DirectorMapper() {
    }

    public static DirectorDto toDto(Director director) {
        return new DirectorDto(director.getId(), director.getName());
    }

    public static List<DirectorDto> toDto(Collection<Director> directors) {
        return directors.stream()
                .map(DirectorMapper::toDto)
                .toList();
    }
}
