package ru.yandex.practicum.filmorate.controller;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    private static final AtomicInteger COUNTER = new AtomicInteger();
    private static final int UNKNOWN_ID = 999999;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String uniqueLogin() {
        return "user" + COUNTER.incrementAndGet() + System.nanoTime();
    }

    private Map<String, Object> userWithLogin(String login) {
        Map<String, Object> user = new LinkedHashMap<>();
        user.put("email", login + "@mail.ru");
        user.put("login", login);
        user.put("name", "Имя");
        user.put("birthday", "1990-05-15");
        return user;
    }

    private String asJson(Object body) throws Exception {
        return objectMapper.writeValueAsString(body);
    }

    private long createUser() throws Exception {
        String response = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJson(userWithLogin(uniqueLogin()))))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return objectMapper.readTree(response).get("id").asLong();
    }

    private long createFilm() throws Exception {
        Map<String, Object> film = new LinkedHashMap<>();
        film.put("name", "Фильм для ленты");
        film.put("description", "Описание");
        film.put("releaseDate", "2000-01-01");
        film.put("duration", 120);
        film.put("mpa", Map.of("id", 1));
        String response = mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJson(film)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return objectMapper.readTree(response).get("id").asLong();
    }

    private long createReview(long userId, long filmId) throws Exception {
        Map<String, Object> review = new LinkedHashMap<>();
        review.put("content", "Отличный фильм");
        review.put("isPositive", true);
        review.put("userId", userId);
        review.put("filmId", filmId);
        String response = mockMvc.perform(post("/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJson(review)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return objectMapper.readTree(response).get("reviewId").asLong();
    }

    private void expectBadRequest(Map<String, Object> user) throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJson(user)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldCreateValidUser() throws Exception {
        String login = uniqueLogin();

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJson(userWithLogin(login))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.login").value(login));
    }

    @Test
    void shouldFailWhenEmailIsEmpty() throws Exception {
        Map<String, Object> user = userWithLogin(uniqueLogin());
        user.put("email", "");

        expectBadRequest(user);
    }

    @Test
    void shouldFailWhenLoginContainsSpaces() throws Exception {
        Map<String, Object> user = userWithLogin(uniqueLogin());
        user.put("login", "login with spaces");

        expectBadRequest(user);
    }

    @Test
    void shouldFailWhenBirthdayIsInFuture() throws Exception {
        Map<String, Object> user = userWithLogin(uniqueLogin());
        user.put("birthday", "2946-08-20");

        expectBadRequest(user);
    }

    @Test
    void shouldFailWhenBirthdayIsMissing() throws Exception {
        Map<String, Object> user = userWithLogin(uniqueLogin());
        user.remove("birthday");

        expectBadRequest(user);
    }

    @Test
    void shouldUseLoginAsNameWhenNameIsEmpty() throws Exception {
        String login = uniqueLogin();
        Map<String, Object> user = userWithLogin(login);
        user.put("name", "");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJson(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(login));
    }

    @Test
    void shouldGetUserById() throws Exception {
        long id = createUser();

        mockMvc.perform(get("/users/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));
    }

    @Test
    void shouldReturn404ForUnknownUser() throws Exception {
        mockMvc.perform(get("/users/" + UNKNOWN_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldAddFriendOneWay() throws Exception {
        long userId = createUser();
        long friendId = createUser();

        mockMvc.perform(put("/users/" + userId + "/friends/" + friendId))
                .andExpect(status().isOk());

        mockMvc.perform(get("/users/" + userId + "/friends"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(friendId));

        mockMvc.perform(get("/users/" + friendId + "/friends"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void shouldRemoveFriend() throws Exception {
        long userId = createUser();
        long friendId = createUser();
        mockMvc.perform(put("/users/" + userId + "/friends/" + friendId));

        mockMvc.perform(delete("/users/" + userId + "/friends/" + friendId))
                .andExpect(status().isOk());

        mockMvc.perform(get("/users/" + userId + "/friends"))
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void shouldReturn404WhenAddingUnknownFriend() throws Exception {
        long userId = createUser();

        mockMvc.perform(put("/users/" + userId + "/friends/" + UNKNOWN_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn404ForFriendsOfUnknownUser() throws Exception {
        mockMvc.perform(get("/users/" + UNKNOWN_ID + "/friends"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldGetCommonFriends() throws Exception {
        long firstId = createUser();
        long secondId = createUser();
        long commonId = createUser();
        mockMvc.perform(put("/users/" + firstId + "/friends/" + commonId));
        mockMvc.perform(put("/users/" + secondId + "/friends/" + commonId));

        mockMvc.perform(get("/users/" + firstId + "/friends/common/" + secondId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(commonId));
    }

    @Test
    void shouldReturn404WhenDeletingUnknownUser() throws Exception {
        // Пытаемся удалить несуществующий фильм
        mockMvc.perform(delete("/users/" + UNKNOWN_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteUserSuccessfully() throws Exception {
        long userId = createUser();

        mockMvc.perform(get("/users/" + userId))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/users/" + userId))
                .andExpect(status().isOk());

        mockMvc.perform(get("/users/" + userId))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnEmptyFeedForUserWithoutEvents() throws Exception {
        long userId = createUser();

        mockMvc.perform(get("/users/" + userId + "/feed"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void shouldReturn404ForFeedOfUnknownUser() throws Exception {
        mockMvc.perform(get("/users/" + UNKNOWN_ID + "/feed"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnFriendEventsInFeed() throws Exception {
        long userId = createUser();
        long friendId = createUser();

        mockMvc.perform(put("/users/" + userId + "/friends/" + friendId))
                .andExpect(status().isOk());
        mockMvc.perform(delete("/users/" + userId + "/friends/" + friendId))
                .andExpect(status().isOk());

        mockMvc.perform(get("/users/" + userId + "/feed"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].eventType").value("FRIEND"))
                .andExpect(jsonPath("$[0].operation").value("ADD"))
                .andExpect(jsonPath("$[0].entityId").value(friendId))
                .andExpect(jsonPath("$[0].userId").value(userId))
                .andExpect(jsonPath("$[1].eventType").value("FRIEND"))
                .andExpect(jsonPath("$[1].operation").value("REMOVE"))
                .andExpect(jsonPath("$[1].entityId").value(friendId));
    }

    @Test
    void shouldCreateEventsForEveryFriendOperation() throws Exception {
        long userId = createUser();
        long friendId = createUser();

        mockMvc.perform(put("/users/" + userId + "/friends/" + friendId))
                .andExpect(status().isOk());
        mockMvc.perform(put("/users/" + userId + "/friends/" + friendId))
                .andExpect(status().isOk());
        mockMvc.perform(delete("/users/" + userId + "/friends/" + friendId))
                .andExpect(status().isOk());
        mockMvc.perform(delete("/users/" + userId + "/friends/" + friendId))
                .andExpect(status().isOk());

        mockMvc.perform(get("/users/" + userId + "/feed"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(4))
                .andExpect(jsonPath("$[0].eventType").value("FRIEND"))
                .andExpect(jsonPath("$[0].operation").value("ADD"))
                .andExpect(jsonPath("$[0].entityId").value(friendId))
                .andExpect(jsonPath("$[0].userId").value(userId))
                .andExpect(jsonPath("$[1].eventType").value("FRIEND"))
                .andExpect(jsonPath("$[1].operation").value("ADD"))
                .andExpect(jsonPath("$[1].entityId").value(friendId))
                .andExpect(jsonPath("$[1].userId").value(userId))
                .andExpect(jsonPath("$[2].operation").value("REMOVE"))
                .andExpect(jsonPath("$[3].operation").value("REMOVE"));
    }

    @Test
    void shouldReturnLikeEventsInFeed() throws Exception {
        long userId = createUser();
        long filmId = createFilm();

        mockMvc.perform(put("/films/" + filmId + "/like/" + userId))
                .andExpect(status().isOk());
        mockMvc.perform(delete("/films/" + filmId + "/like/" + userId))
                .andExpect(status().isOk());

        mockMvc.perform(get("/users/" + userId + "/feed"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].eventType").value("LIKE"))
                .andExpect(jsonPath("$[0].operation").value("ADD"))
                .andExpect(jsonPath("$[0].entityId").value(filmId))
                .andExpect(jsonPath("$[0].userId").value(userId))
                .andExpect(jsonPath("$[1].eventType").value("LIKE"))
                .andExpect(jsonPath("$[1].operation").value("REMOVE"))
                .andExpect(jsonPath("$[1].entityId").value(filmId));
    }

    @Test
    void shouldReturnReviewEventsInFeed() throws Exception {
        long userId = createUser();
        long filmId = createFilm();
        long reviewId = createReview(userId, filmId);

        Map<String, Object> update = new LinkedHashMap<>();
        update.put("reviewId", reviewId);
        update.put("content", "Обновлённый отзыв");
        update.put("isPositive", false);
        update.put("userId", userId);
        update.put("filmId", filmId);

        mockMvc.perform(put("/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJson(update)))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/reviews/" + reviewId))
                .andExpect(status().isOk());

        mockMvc.perform(get("/users/" + userId + "/feed"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].eventType").value("REVIEW"))
                .andExpect(jsonPath("$[0].operation").value("ADD"))
                .andExpect(jsonPath("$[0].entityId").value(reviewId))
                .andExpect(jsonPath("$[1].eventType").value("REVIEW"))
                .andExpect(jsonPath("$[1].operation").value("UPDATE"))
                .andExpect(jsonPath("$[1].entityId").value(reviewId))
                .andExpect(jsonPath("$[2].eventType").value("REVIEW"))
                .andExpect(jsonPath("$[2].operation").value("REMOVE"))
                .andExpect(jsonPath("$[2].entityId").value(reviewId));
    }
}
