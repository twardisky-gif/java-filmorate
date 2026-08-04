package ru.yandex.practicum.filmorate.mapper;

import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public final class MpaMapper {

    private MpaMapper() {
    }

    public static MpaDto toDto(Mpa mpa) {
        return new MpaDto(mpa.getId(), mpa.getName());
    }

    public static List<MpaDto> toDto(List<Mpa> mpaList) {
        return mpaList.stream()
                .map(MpaMapper::toDto)
                .toList();
    }
}
