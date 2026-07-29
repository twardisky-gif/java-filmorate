package ru.yandex.practicum.filmorate.controller;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {
    private UserController controller;

    @BeforeEach
    void setUp() {
        controller = new UserController();
    }

    private User createValidUser() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testlogin");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 5, 15));
        return user;
    }

    @Test
    void shouldCreateValidUser() {
        User user = createValidUser();
        User created = controller.create(user);

        assertEquals(1, created.getId());
        assertEquals("testlogin", created.getLogin());
    }

    @Test
    void shouldFailWhenEmailIsEmpty() {
        User user = createValidUser();
        user.setEmail("");

        assertThrows(ValidationException.class, () -> controller.create(user));
    }

    @Test
    void shouldFailWhenEmailIsNull() {
        User user = createValidUser();
        user.setEmail(null);

        assertThrows(ValidationException.class, () -> controller.create(user));
    }

    @Test
    void shouldFailWhenEmailHasNoAt() {
        User user = createValidUser();
        user.setEmail("invalid-email");

        assertThrows(ValidationException.class, () -> controller.create(user));
    }

    @Test
    void shouldFailWhenLoginIsEmpty() {
        User user = createValidUser();
        user.setLogin("");

        assertThrows(ValidationException.class, () -> controller.create(user));
    }

    @Test
    void shouldFailWhenLoginIsNull() {
        User user = createValidUser();
        user.setLogin(null);

        assertThrows(ValidationException.class, () -> controller.create(user));
    }

    @Test
    void shouldFailWhenLoginContainsSpaces() {
        User user = createValidUser();
        user.setLogin("login with spaces");

        assertThrows(ValidationException.class, () -> controller.create(user));
    }

    @Test
    void shouldUseLoginAsNameWhenNameIsEmpty() {
        User user = createValidUser();
        user.setName("");

        User created = controller.create(user);
        assertEquals("testlogin", created.getName());
    }

    @Test
    void shouldUseLoginAsNameWhenNameIsNull() {
        User user = createValidUser();
        user.setName(null);

        User created = controller.create(user);
        assertEquals("testlogin", created.getName());
    }

    @Test
    void shouldFailWhenBirthdayIsInFuture() {
        User user = createValidUser();
        user.setBirthday(LocalDate.now().plusDays(1));

        assertThrows(ValidationException.class, () -> controller.create(user));
    }

    @Test
    void shouldAcceptBirthdayToday() {
        User user = createValidUser();
        user.setBirthday(LocalDate.now());

        User created = controller.create(user);
        assertNotNull(created);
    }

    @Test
    void shouldGetAllUsers() {
        User user1 = createValidUser();
        User user2 = createValidUser();
        user2.setLogin("another");
        user2.setEmail("another@test.com");

        controller.create(user1);
        controller.create(user2);

        assertEquals(2, controller.getAll().size());
    }

    @Test
    void shouldUpdateUser() {
        User user = createValidUser();
        User created = controller.create(user);

        created.setName("Updated Name");
        User updated = controller.update(created);

        assertEquals("Updated Name", updated.getName());
    }

    @Test
    void shouldFailUpdateWithUnknownId() {
        User user = createValidUser();
        user.setId(999);

        assertThrows(NotFoundException.class, () -> controller.update(user));
    }
}
