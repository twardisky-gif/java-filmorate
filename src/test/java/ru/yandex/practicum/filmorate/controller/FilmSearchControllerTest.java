package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class FilmSearchControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldSearchByTitleIgnoringCase() throws Exception {
        long matchingFilmId = createFilm("Тигр TitleMarker", null);
        createFilm("Другой фильм", null);

        mockMvc.perform(get("/films/search")
                        .param("query", "TITLEMARKER")
                        .param("by", "title"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(matchingFilmId));
    }

    @Test
    void shouldSearchByDirectorIgnoringCase() throws Exception {
        long matchingDirectorId = createDirector("Режиссёр DirectorMarker");
        long otherDirectorId = createDirector("Другой режиссёр");
        long matchingFilmId = createFilm("Первый фильм", matchingDirectorId);
        createFilm("Второй фильм", otherDirectorId);

        mockMvc.perform(get("/films/search")
                        .param("query", "DIRECTORMARKER")
                        .param("by", "director"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(matchingFilmId));
    }

    @Test
    void shouldSearchByTitleAndDirectorWithoutDuplicates() throws Exception {
        long directorId = createDirector("Режиссёр CombinedMarker");
        long matchingBothId = createFilm("Фильм CombinedMarker", directorId);
        long matchingTitleId = createFilm("Ночь CombinedMarker", null);
        long userId = createUser("first-search-user@mail.ru", "first-search-user");
        addLike(matchingTitleId, userId);

        mockMvc.perform(get("/films/search")
                        .param("query", "combinedmarker")
                        .param("by", "director,title"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(matchingTitleId))
                .andExpect(jsonPath("$[1].id").value(matchingBothId));
    }

    @Test
    void shouldReturnEmptyListForBlankQuery() throws Exception {
        createFilm("Фильм", null);

        mockMvc.perform(get("/films/search")
                        .param("query", " ")
                        .param("by", "title"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void shouldRejectUnknownSearchType() throws Exception {
        mockMvc.perform(get("/films/search")
                        .param("query", "фильм")
                        .param("by", "description"))
                .andExpect(status().isBadRequest());
    }

    private long createDirector(String name) throws Exception {
        String response = mockMvc.perform(post("/directors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("name", name))))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return objectMapper.readTree(response).get("id").asLong();
    }

    private long createFilm(String name, Long directorId) throws Exception {
        Map<String, Object> film = new java.util.HashMap<>();
        film.put("name", name);
        film.put("description", "Описание");
        film.put("releaseDate", "2000-01-01");
        film.put("duration", 120);
        film.put("mpa", Map.of("id", 3));
        if (directorId != null) {
            film.put("directors", List.of(Map.of("id", directorId)));
        }
        String response = mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return objectMapper.readTree(response).get("id").asLong();
    }

    private long createUser(String email, String login) throws Exception {
        String response = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "email", email,
                                "login", login,
                                "name", "Пользователь",
                                "birthday", "1990-01-01"))))
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
}
