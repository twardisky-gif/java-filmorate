package ru.yandex.practicum.filmorate.storage.friendship;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.BaseRepository;
import ru.yandex.practicum.filmorate.storage.mappers.UserRowMapper;

@Repository
public class FriendshipDbStorage extends BaseRepository<User> implements FriendshipStorage {
    private static final String ADD_QUERY =
            "MERGE INTO friendships (user_id, friend_id, status) KEY (user_id, friend_id) VALUES (?, ?, FALSE)";
    private static final String REMOVE_QUERY =
            "DELETE FROM friendships WHERE user_id = ? AND friend_id = ?";
    private static final String FIND_FRIENDS_QUERY =
            "SELECT u.user_id, u.email, u.login, u.name, u.birthday "
                    + "FROM friendships f "
                    + "JOIN users u ON u.user_id = f.friend_id "
                    + "WHERE f.user_id = ? "
                    + "ORDER BY u.user_id";
    private static final String FIND_COMMON_FRIENDS_QUERY =
            "SELECT u.user_id, u.email, u.login, u.name, u.birthday "
                    + "FROM friendships f1 "
                    + "JOIN friendships f2 ON f1.friend_id = f2.friend_id "
                    + "JOIN users u ON u.user_id = f1.friend_id "
                    + "WHERE f1.user_id = ? AND f2.user_id = ? "
                    + "ORDER BY u.user_id";

    public FriendshipDbStorage(JdbcTemplate jdbc, UserRowMapper mapper) {
        super(jdbc, mapper);
    }

    @Override
    public void add(long userId, long friendId) {
        jdbc.update(ADD_QUERY, userId, friendId);
    }

    @Override
    public void remove(long userId, long friendId) {
        delete(REMOVE_QUERY, userId, friendId);
    }

    @Override
    public List<User> getFriends(long userId) {
        return findMany(FIND_FRIENDS_QUERY, userId);
    }

    @Override
    public List<User> getCommonFriends(long userId, long otherId) {
        return findMany(FIND_COMMON_FRIENDS_QUERY, userId, otherId);
    }
}
