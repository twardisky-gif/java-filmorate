package ru.yandex.practicum.filmorate.storage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({UserDbStorage.class, UserRowMapper.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDbStorageTest {

    private final UserDbStorage userStorage;

    private User newUser(String login) {
        User user = new User();
        user.setEmail(login + "@mail.ru");
        user.setLogin(login);
        user.setName("Имя " + login);
        user.setBirthday(LocalDate.of(1990, 5, 15));
        return user;
    }

    @Test
    void shouldCreateUser() {
        User created = userStorage.add(newUser("create"));

        assertThat(created.getId()).isPositive();
        assertThat(created.getLogin()).isEqualTo("create");
    }

    @Test
    void shouldFindUserById() {
        User created = userStorage.add(newUser("findbyid"));

        Optional<User> found = userStorage.getById(created.getId());

        assertThat(found)
                .isPresent()
                .hasValueSatisfying(user -> {
                    assertThat(user).hasFieldOrPropertyWithValue("id", created.getId());
                    assertThat(user).hasFieldOrPropertyWithValue("login", "findbyid");
                    assertThat(user).hasFieldOrPropertyWithValue("email", "findbyid@mail.ru");
                });
    }

    @Test
    void shouldReturnEmptyForUnknownId() {
        assertThat(userStorage.getById(9999L)).isEmpty();
    }

    @Test
    void shouldUpdateUser() {
        User created = userStorage.add(newUser("beforeupdate"));
        created.setName("Новое имя");
        created.setEmail("afterupdate@mail.ru");

        userStorage.update(created);

        assertThat(userStorage.getById(created.getId()))
                .isPresent()
                .hasValueSatisfying(user -> {
                    assertThat(user.getName()).isEqualTo("Новое имя");
                    assertThat(user.getEmail()).isEqualTo("afterupdate@mail.ru");
                });
    }

    @Test
    void shouldFindAllUsers() {
        userStorage.add(newUser("first"));
        userStorage.add(newUser("second"));

        Collection<User> users = userStorage.getAll();

        assertThat(users).hasSize(2);
        assertThat(users).extracting(User::getLogin).containsExactlyInAnyOrder("first", "second");
    }

    @Test
    void shouldDeleteUser() {
        User created = userStorage.add(newUser("todelete"));

        userStorage.delete(created.getId());

        assertThat(userStorage.getById(created.getId())).isEmpty();
    }
}
