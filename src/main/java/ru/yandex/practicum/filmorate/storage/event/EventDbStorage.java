package ru.yandex.practicum.filmorate.storage.event;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventOperation;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.storage.BaseRepository;
import ru.yandex.practicum.filmorate.storage.mappers.EventRowMapper;

import java.util.List;

@Repository
public class EventDbStorage extends BaseRepository<Event> implements EventStorage {
    private static final String INSERT_QUERY =
            "INSERT INTO events (user_id, event_type, operation, entity_id, event_timestamp) "
                    + "VALUES (?, ?, ?, ?, ?)";

    private static final String FIND_BY_USER_QUERY =
            "SELECT event_id, user_id, event_type, operation, entity_id, event_timestamp "
                    + "FROM events WHERE user_id = ? "
                    + "ORDER BY event_timestamp ASC, event_id ASC";

    public EventDbStorage(JdbcTemplate jdbc, EventRowMapper mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Event add(long userId, EventType eventType, EventOperation operation, long entityId) {
        long timestamp = System.currentTimeMillis();
        long eventId = insert(INSERT_QUERY, userId, eventType.name(), operation.name(), entityId, timestamp);

        Event event = new Event();
        event.setEventId(eventId);
        event.setTimestamp(timestamp);
        event.setUserId(userId);
        event.setEventType(eventType);
        event.setOperation(operation);
        event.setEntityId(entityId);
        return event;
    }

    @Override
    public List<Event> findByUserId(long userId) {
        return findMany(FIND_BY_USER_QUERY, userId);
    }
}
