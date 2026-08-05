package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.EventDto;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.mapper.EventMapper;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserControllerImpl implements UserController {
    private final UserService userService;
    private final FilmService filmService;

    public UserControllerImpl(UserService userService, FilmService filmService) {
        this.userService = userService;
        this.filmService = filmService;
    }

    @Override
    @GetMapping
    public Collection<UserDto> getAll() {
        return UserMapper.toDto(userService.getAll());
    }

    @Override
    @GetMapping("/{id}")
    public UserDto getById(@PathVariable long id) {
        return UserMapper.toDto(userService.getById(id));
    }

    @Override
    @PostMapping
    public UserDto create(@Valid @RequestBody UserDto user) {
        return UserMapper.toDto(userService.create(UserMapper.toModel(user)));
    }

    @Override
    @PutMapping
    public UserDto update(@Valid @RequestBody UserDto user) {
        return UserMapper.toDto(userService.update(UserMapper.toModel(user)));
    }

    @Override
    @DeleteMapping("/{id}")
    public void removeUser(@PathVariable long id) {
        userService.removeUser(id);
    }

    @Override
    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable long id, @PathVariable long friendId) {
        userService.addFriend(id, friendId);
    }

    @Override
    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable long id, @PathVariable long friendId) {
        userService.removeFriend(id, friendId);
    }

    @Override
    @GetMapping("/{id}/friends")
    public List<UserDto> getFriends(@PathVariable long id) {
        return UserMapper.toDto(userService.getFriends(id));
    }

    @Override
    @GetMapping("/{id}/friends/common/{otherId}")
    public List<UserDto> getCommonFriends(@PathVariable long id, @PathVariable long otherId) {
        return UserMapper.toDto(userService.getCommonFriends(id, otherId));
    }

    @Override
    @GetMapping("/{id}/recommendations")
    public List<FilmDto> getRecommendations(@PathVariable long id,
                                            @RequestParam(defaultValue = "10") int count) {
        return FilmMapper.toDto(filmService.getRecommendations(id, count));
    }

    @Override
    @GetMapping("/{id}/feed")
    public List<EventDto> getFeed(@PathVariable long id) {
        return EventMapper.toDto(userService.getFeed(id));
    }
}
