package ru.yandex.practicum.filmorate.storage;

import java.time.LocalDate;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventOperation;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.event.EventDbStorage;
import ru.yandex.practicum.filmorate.storage.mappers.EventRowMapper;
import ru.yandex.practicum.filmorate.storage.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({EventDbStorage.class, EventRowMapper.class, UserDbStorage.class, UserRowMapper.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class EventDbStorageTest {

    private final EventDbStorage eventStorage;
    private final UserDbStorage userStorage;

    private User newUser(String login) {
        User user = new User();
        user.setEmail(login + "@mail.ru");
        user.setLogin(login);
        user.setName("Имя " + login);
        user.setBirthday(LocalDate.of(1990, 5, 15));
        return userStorage.add(user);
    }

    @Test
    void shouldAddAndFindEventsByUserOrderedByTimestamp() {
        User user = newUser("actor");
        User other = newUser("other");

        Event first = eventStorage.add(user.getId(), EventType.FRIEND, EventOperation.ADD, other.getId());
        Event second = eventStorage.add(user.getId(), EventType.LIKE, EventOperation.ADD, 10L);

        assertThat(first.getEventId()).isPositive();
        assertThat(first.getTimestamp()).isPositive();
        assertThat(first.getUserId()).isEqualTo(user.getId());
        assertThat(first.getEventType()).isEqualTo(EventType.FRIEND);
        assertThat(first.getOperation()).isEqualTo(EventOperation.ADD);
        assertThat(first.getEntityId()).isEqualTo(other.getId());

        List<Event> feed = eventStorage.findByUserId(user.getId());
        assertThat(feed).extracting(Event::getEventId)
                .containsExactly(first.getEventId(), second.getEventId());

        assertThat(eventStorage.findByUserId(other.getId())).isEmpty();
    }

    @Test
    void shouldStoreRemoveAndUpdateOperations() {
        User user = newUser("ops");

        eventStorage.add(user.getId(), EventType.LIKE, EventOperation.REMOVE, 5L);
        eventStorage.add(user.getId(), EventType.REVIEW, EventOperation.UPDATE, 7L);

        List<Event> feed = eventStorage.findByUserId(user.getId());
        assertThat(feed).extracting(Event::getOperation)
                .containsExactly(EventOperation.REMOVE, EventOperation.UPDATE);
        assertThat(feed).extracting(Event::getEventType)
                .containsExactly(EventType.LIKE, EventType.REVIEW);
    }
}
