package ru.yandex.practicum.filmorate.controller;

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

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String userJson(String login) {
        return """
                {"email":"%s@mail.ru","login":"%s","name":"Имя","birthday":"1990-05-15"}
                """.formatted(login, login);
    }

    private String uniqueLogin() {
        return "user" + COUNTER.incrementAndGet() + System.nanoTime();
    }

    private long createUser() throws Exception {
        String body = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson(uniqueLogin())))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return objectMapper.readTree(body).get("id").asLong();
    }

    private void expectBadRequest(String body) throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldCreateValidUser() throws Exception {
        String login = uniqueLogin();

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.login").value(login));
    }

    @Test
    void shouldFailWhenEmailIsEmpty() throws Exception {
        expectBadRequest("""
                {"email":"","login":"login","name":"Имя","birthday":"1990-05-15"}
                """);
    }

    @Test
    void shouldFailWhenLoginContainsSpaces() throws Exception {
        expectBadRequest("""
                {"email":"spaces@mail.ru","login":"login with spaces","name":"Имя","birthday":"1990-05-15"}
                """);
    }

    @Test
    void shouldFailWhenBirthdayIsInFuture() throws Exception {
        expectBadRequest("""
                {"email":"future@mail.ru","login":"future","name":"Имя","birthday":"2946-08-20"}
                """);
    }

    @Test
    void shouldUseLoginAsNameWhenNameIsEmpty() throws Exception {
        String login = uniqueLogin();

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"%s@mail.ru","login":"%s","name":"","birthday":"1990-05-15"}
                                """.formatted(login, login)))
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
        mockMvc.perform(get("/users/999999"))
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

        mockMvc.perform(put("/users/" + userId + "/friends/999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn404ForFriendsOfUnknownUser() throws Exception {
        mockMvc.perform(get("/users/999999/friends"))
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
}
