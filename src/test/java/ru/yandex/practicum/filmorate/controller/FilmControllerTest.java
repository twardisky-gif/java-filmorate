package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.greaterThan;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class FilmControllerTest {

    private static final String VALID_FILM = """
            {"name":"Тестовый фильм","description":"Описание","releaseDate":"2000-01-01",
             "duration":120,"mpa":{"id":3},"genres":[{"id":1},{"id":2}]}
            """;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private long createFilm() throws Exception {
        String body = mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_FILM))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return objectMapper.readTree(body).get("id").asLong();
    }

    private void expectStatus(String body, int expectedStatus) throws Exception {
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().is(expectedStatus));
    }

    @Test
    void shouldCreateValidFilm() throws Exception {
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_FILM))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Тестовый фильм"))
                .andExpect(jsonPath("$.mpa.id").value(3))
                .andExpect(jsonPath("$.mpa.name").value("PG-13"))
                .andExpect(jsonPath("$.genres.length()").value(2))
                .andExpect(jsonPath("$.genres[0].id").value(1))
                .andExpect(jsonPath("$.genres[0].name").value("Комедия"));
    }

    @Test
    void shouldFailWhenNameIsEmpty() throws Exception {
        expectStatus("""
                {"name":"","description":"Описание","releaseDate":"2000-01-01",
                 "duration":120,"mpa":{"id":1}}
                """, 400);
    }

    @Test
    void shouldFailWhenDescriptionIsTooLong() throws Exception {
        expectStatus("""
                {"name":"Фильм","description":"%s","releaseDate":"2000-01-01",
                 "duration":120,"mpa":{"id":1}}
                """.formatted("a".repeat(201)), 400);
    }

    @Test
    void shouldFailWhenReleaseDateIsTooEarly() throws Exception {
        expectStatus("""
                {"name":"Фильм","description":"Описание","releaseDate":"1895-12-27",
                 "duration":120,"mpa":{"id":1}}
                """, 400);
    }

    @Test
    void shouldFailWhenDurationIsNegative() throws Exception {
        expectStatus("""
                {"name":"Фильм","description":"Описание","releaseDate":"2000-01-01",
                 "duration":-1,"mpa":{"id":1}}
                """, 400);
    }

    @Test
    void shouldFailWhenMpaIsMissing() throws Exception {
        expectStatus("""
                {"name":"Фильм","description":"Описание",
                 "releaseDate":"2000-01-01","duration":120}
                """, 400);
    }

    @Test
    void shouldReturn404ForUnknownMpa() throws Exception {
        expectStatus("""
                {"name":"Фильм","description":"Описание","releaseDate":"2000-01-01",
                 "duration":120,"mpa":{"id":9999}}
                """, 404);
    }

    @Test
    void shouldReturn404ForUnknownGenre() throws Exception {
        expectStatus("""
                {"name":"Фильм","description":"Описание","releaseDate":"2000-01-01",
                 "duration":120,"mpa":{"id":1},"genres":[{"id":9999}]}
                """, 404);
    }

    @Test
    void shouldNotDuplicateGenres() throws Exception {
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Без дублей","description":"Описание","releaseDate":"2000-01-01",
                                 "duration":120,"mpa":{"id":3},"genres":[{"id":1},{"id":2},{"id":1}]}
                                """))
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
        mockMvc.perform(get("/films/999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn404WhenUpdatingUnknownFilm() throws Exception {
        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"id":999999,"name":"Фильм","description":"Описание",
                                 "releaseDate":"2000-01-01","duration":120,"mpa":{"id":1}}
                                """))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldUpdateFilm() throws Exception {
        long id = createFilm();

        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"id":%d,"name":"Обновленный","description":"Новое описание",
                                 "releaseDate":"1989-04-17","duration":190,"mpa":{"id":1}}
                                """.formatted(id)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Обновленный"))
                .andExpect(jsonPath("$.mpa.name").value("G"));
    }

    @Test
    void shouldReturnPopularFilms() throws Exception {
        createFilm();

        mockMvc.perform(get("/films/popular"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(greaterThan(0)));
    }
}
