package ru.yandex.practicum.filmorate.storage.like;

public interface LikeStorage {
    void add(long filmId, long userId);

    void remove(long filmId, long userId);
}
