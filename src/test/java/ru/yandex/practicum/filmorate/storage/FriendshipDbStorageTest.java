package ru.yandex.practicum.filmorate.storage;

import java.time.LocalDate;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.friendship.FriendshipDbStorage;
import ru.yandex.practicum.filmorate.storage.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({FriendshipDbStorage.class, UserDbStorage.class, UserRowMapper.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FriendshipDbStorageTest {

    private final FriendshipDbStorage friendshipStorage;
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
    void shouldAddFriendOneWay() {
        User user = newUser("owner");
        User friend = newUser("friend");

        friendshipStorage.add(user.getId(), friend.getId());

        assertThat(friendshipStorage.getFriends(user.getId()))
                .extracting(User::getId)
                .containsExactly(friend.getId());
        assertThat(friendshipStorage.getFriends(friend.getId())).isEmpty();
    }

    @Test
    void shouldNotDuplicateFriendship() {
        User user = newUser("owner");
        User friend = newUser("friend");

        friendshipStorage.add(user.getId(), friend.getId());
        friendshipStorage.add(user.getId(), friend.getId());

        assertThat(friendshipStorage.getFriends(user.getId())).hasSize(1);
    }

    @Test
    void shouldRemoveFriend() {
        User user = newUser("owner");
        User friend = newUser("friend");
        friendshipStorage.add(user.getId(), friend.getId());

        friendshipStorage.remove(user.getId(), friend.getId());

        assertThat(friendshipStorage.getFriends(user.getId())).isEmpty();
    }

    @Test
    void shouldReturnEmptyFriendsForNewUser() {
        User user = newUser("lonely");

        assertThat(friendshipStorage.getFriends(user.getId())).isEmpty();
    }

    @Test
    void shouldFindCommonFriends() {
        User first = newUser("first");
        User second = newUser("second");
        User common = newUser("common");
        User personal = newUser("personal");
        friendshipStorage.add(first.getId(), common.getId());
        friendshipStorage.add(second.getId(), common.getId());
        friendshipStorage.add(first.getId(), personal.getId());

        List<User> commonFriends = friendshipStorage.getCommonFriends(first.getId(), second.getId());

        assertThat(commonFriends)
                .extracting(User::getLogin)
                .containsExactly("common");
    }

    @Test
    void shouldReturnEmptyCommonFriendsWhenNoIntersection() {
        User first = newUser("first");
        User second = newUser("second");
        User friend = newUser("friend");
        friendshipStorage.add(first.getId(), friend.getId());

        assertThat(friendshipStorage.getCommonFriends(first.getId(), second.getId())).isEmpty();
    }
}
