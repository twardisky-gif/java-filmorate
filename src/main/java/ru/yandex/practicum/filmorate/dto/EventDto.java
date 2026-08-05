package ru.yandex.practicum.filmorate.dto;

import ru.yandex.practicum.filmorate.model.EventOperation;
import ru.yandex.practicum.filmorate.model.EventType;

public record EventDto(long eventId,
                       long timestamp,
                       long userId,
                       EventType eventType,
                       EventOperation operation,
                       long entityId) {
}
