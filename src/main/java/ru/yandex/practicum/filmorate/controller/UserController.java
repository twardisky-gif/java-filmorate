package ru.yandex.practicum.filmorate.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private int nextId = 1;

    @GetMapping
    public Collection<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User create(@RequestBody User user) {
        validate(user);
        applyNameIfEmpty(user);
        user.setId(nextId++);
        users.put(user.getId(), user);
        log.info("Создан пользователь: {}", user.getLogin());
        return user;
    }

    @PutMapping
    public User update(@RequestBody User user) {
        int id = user.getId();
        if (!users.containsKey(id)) {
            throw new NotFoundException("Пользователь с id=" + id + " не найден");
        }
        validate(user);
        applyNameIfEmpty(user);
        users.put(id, user);
        log.info("Обновлен пользователь: {}", user.getLogin());
        return user;
    }

    private void validate(User user) {
        String email = user.getEmail();
        if (email == null || email.isBlank() || !email.contains("@")) {
            log.warn("Некорректный email: {}", email);
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать @");
        }
        String login = user.getLogin();
        if (login == null || login.isBlank() || login.contains(" ")) {
            log.warn("Некорректный логин: {}", login);
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }
        LocalDate birthday = user.getBirthday();
        if (birthday != null && birthday.isAfter(LocalDate.now())) {
            log.warn("Дата рождения в будущем: {}", birthday);
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }

    private void applyNameIfEmpty(User user) {
        String name = user.getName();
        if (name == null || name.isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
