package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class Event {
    private long eventId;
    private long timestamp;
    private long userId;
    private EventType eventType;
    private EventOperation operation;
    private long entityId;
}
