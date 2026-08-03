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
}
