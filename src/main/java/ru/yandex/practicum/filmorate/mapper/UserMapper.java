package ru.yandex.practicum.filmorate.mapper;

import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;

public final class UserMapper {
    private UserMapper() {
    }

    public static UserDto toDto(User user) {
        return new UserDto(user.getId(), user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
    }

    public static List<UserDto> toDto(Collection<User> users) {
        return users.stream()
                .map(UserMapper::toDto)
                .toList();
    }

    public static User toModel(UserDto userDto) {
        User user = new User();
        user.setId(userDto.id());
        user.setEmail(userDto.email());
        user.setLogin(userDto.login());
        user.setName(userDto.name());
        user.setBirthday(userDto.birthday());
        return user;
    }
}
