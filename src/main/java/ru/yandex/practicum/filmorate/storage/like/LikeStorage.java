package ru.yandex.practicum.filmorate.storage.like;

public interface LikeStorage {
    boolean add(long filmId, long userId);

    boolean remove(long filmId, long userId);
}
