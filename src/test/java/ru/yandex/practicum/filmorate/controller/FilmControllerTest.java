package ru.yandex.practicum.filmorate.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.hamcrest.Matchers.greaterThan;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class FilmControllerTest {

    private static final int MPA_G = 1;
    private static final int MPA_PG13 = 3;
    private static final int GENRE_COMEDY = 1;
    private static final int GENRE_DRAMA = 2;
    private static final int UNKNOWN_ID = 999999;
    private static final int MAX_DESCRIPTION_LENGTH = 200;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Map<String, Object> validFilm() {
        Map<String, Object> film = new LinkedHashMap<>();
        film.put("name", "Тестовый фильм");
        film.put("description", "Описание");
        film.put("releaseDate", "2000-01-01");
        film.put("duration", 120);
        film.put("mpa", Map.of("id", MPA_PG13));
        film.put("genres", List.of(Map.of("id", GENRE_COMEDY), Map.of("id", GENRE_DRAMA)));
        return film;
    }

    private Map<String, Object> validUser(String email, String login, String name) {
        Map<String, Object> user = new LinkedHashMap<>();
        user.put("email", email);
        user.put("login", login);
        user.put("name", name);
        user.put("birthday", "1990-01-01");
        return user;
    }

    private String asJson(Object body) throws Exception {
        return objectMapper.writeValueAsString(body);
    }

    private long createFilm(Map<String, Object> filmData) throws Exception {
        String response = mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJson(filmData)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return objectMapper.readTree(response).get("id").asLong();
    }

    private long createFilm() throws Exception {
        return createFilm(validFilm());
    }

    private long createUser(String email, String login, String name) throws Exception {
        String response = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJson(validUser(email, login, name))))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return objectMapper.readTree(response).get("id").asLong();
    }

    private void addLike(long filmId, long userId) throws Exception {
        mockMvc.perform(put("/films/{filmId}/like/{userId}", filmId, userId))
                .andExpect(status().isOk());
    }

    private void expectCreateStatus(Map<String, Object> film, int expectedStatus) throws Exception {
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJson(film)))
                .andExpect(status().is(expectedStatus));
    }

    @Test
    void shouldCreateValidFilm() throws Exception {
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJson(validFilm())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Тестовый фильм"))
                .andExpect(jsonPath("$.mpa.id").value(MPA_PG13))
                .andExpect(jsonPath("$.mpa.name").value("PG-13"))
                .andExpect(jsonPath("$.genres.length()").value(2))
                .andExpect(jsonPath("$.genres[0].id").value(GENRE_COMEDY))
                .andExpect(jsonPath("$.genres[0].name").value("Комедия"));
    }

    @Test
    void shouldFailWhenNameIsEmpty() throws Exception {
        Map<String, Object> film = validFilm();
        film.put("name", "");

        expectCreateStatus(film, 400);
    }

    @Test
    void shouldFailWhenDescriptionIsTooLong() throws Exception {
        Map<String, Object> film = validFilm();
        film.put("description", "a".repeat(MAX_DESCRIPTION_LENGTH + 1));

        expectCreateStatus(film, 400);
    }

    @Test
    void shouldFailWhenReleaseDateIsTooEarly() throws Exception {
        Map<String, Object> film = validFilm();
        film.put("releaseDate", "1895-12-27");

        expectCreateStatus(film, 400);
    }

    @Test
    void shouldFailWhenDurationIsNegative() throws Exception {
        Map<String, Object> film = validFilm();
        film.put("duration", -1);

        expectCreateStatus(film, 400);
    }

    @Test
    void shouldFailWhenMpaIsMissing() throws Exception {
        Map<String, Object> film = validFilm();
        film.remove("mpa");

        expectCreateStatus(film, 400);
    }

    @Test
    void shouldReturn404ForUnknownMpa() throws Exception {
        Map<String, Object> film = validFilm();
        film.put("mpa", Map.of("id", UNKNOWN_ID));

        expectCreateStatus(film, 404);
    }

    @Test
    void shouldReturn404ForUnknownGenre() throws Exception {
        Map<String, Object> film = validFilm();
        film.put("genres", List.of(Map.of("id", UNKNOWN_ID)));

        expectCreateStatus(film, 404);
    }

    @Test
    void shouldNotDuplicateGenres() throws Exception {
        Map<String, Object> film = validFilm();
        film.put("genres", List.of(
                Map.of("id", GENRE_COMEDY),
                Map.of("id", GENRE_DRAMA),
                Map.of("id", GENRE_COMEDY)));

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJson(film)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.genres.length()").value(2));
    }

    @Test
    void shouldGetFilmById() throws Exception {
        long id = createFilm();

        mockMvc.perform(get("/films/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("Тестовый фильм"));
    }

    @Test
    void shouldReturn404ForUnknownFilm() throws Exception {
        mockMvc.perform(get("/films/" + UNKNOWN_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldUpdateFilm() throws Exception {
        Map<String, Object> film = validFilm();
        film.put("id", createFilm());
        film.put("name", "Обновленный");
        film.put("mpa", Map.of("id", MPA_G));

        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJson(film)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Обновленный"))
                .andExpect(jsonPath("$.mpa.name").value("G"));
    }

    @Test
    void shouldReturn404WhenUpdatingUnknownFilm() throws Exception {
        Map<String, Object> film = validFilm();
        film.put("id", UNKNOWN_ID);

        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJson(film)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnPopularFilms() throws Exception {
        createFilm();

        mockMvc.perform(get("/films/popular"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(greaterThan(0)));
    }

    @Test
    void shouldReturn404WhenDeletingUnknownFilm() throws Exception {
        // Пытаемся удалить несуществующий фильм
        mockMvc.perform(delete("/films/" + UNKNOWN_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteFilmSuccessfully() throws Exception {
        long filmId = createFilm();

        mockMvc.perform(get("/films/" + filmId))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/films/" + filmId))
                .andExpect(status().isOk());

        mockMvc.perform(get("/films/" + filmId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /users/{id}/recommendations должен возвращать рекомендации")
    void shouldReturnRecommendations() throws Exception {
        long user1Id = createUser("user1@test.com", "user1", "User One");
        long user2Id = createUser("user2@test.com", "user2", "User Two");

        Map<String, Object> film1Data = validFilm();
        film1Data.put("name", "Фильм 1");
        Map<String, Object> film2Data = validFilm();
        film2Data.put("name", "Фильм 2");
        Map<String, Object> film3Data = validFilm();
        film3Data.put("name", "Фильм 3");

        long film1Id = createFilm(film1Data);
        long film2Id = createFilm(film2Data);
        long film3Id = createFilm(film3Data);

        addLike(film1Id, user1Id);
        addLike(film2Id, user1Id);

        addLike(film1Id, user2Id);
        addLike(film3Id, user2Id);

        mockMvc.perform(get("/users/{id}/recommendations", user1Id)
                        .param("count", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(film3Id))
                .andExpect(jsonPath("$[0].name").value("Фильм 3"));

        mockMvc.perform(get("/users/{id}/recommendations", user2Id)
                        .param("count", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(film2Id))
                .andExpect(jsonPath("$[0].name").value("Фильм 2"));
    }
}