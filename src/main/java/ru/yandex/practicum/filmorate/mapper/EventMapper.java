package ru.yandex.practicum.filmorate.mapper;

import ru.yandex.practicum.filmorate.dto.EventDto;
import ru.yandex.practicum.filmorate.model.Event;

import java.util.Collection;
import java.util.List;

public final class EventMapper {
    private EventMapper() {
    }

    public static EventDto toDto(Event event) {
        return new EventDto(event.getEventId(), event.getTimestamp(), event.getUserId(), event.getEventType(),
                event.getOperation(), event.getEntityId());
    }

    public static List<EventDto> toDto(Collection<Event> events) {
        return events.stream()
                .map(EventMapper::toDto)
                .toList();
    }
}
