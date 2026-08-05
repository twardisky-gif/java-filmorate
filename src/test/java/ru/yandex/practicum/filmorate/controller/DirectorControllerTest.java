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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class DirectorControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateUpdateAndDeleteDirector() throws Exception {
        long id = createDirector("Режиссёр");

        mockMvc.perform(get("/directors/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("Режиссёр"));

        mockMvc.perform(put("/directors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "id", id,
                                "name", "Новое имя"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Новое имя"));

        mockMvc.perform(delete("/directors/{id}", id))
                .andExpect(status().isOk());

        mockMvc.perform(get("/directors/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldRejectBlankNameAndUnknownDirector() throws Exception {
        mockMvc.perform(post("/directors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("name", " "))))
                .andExpect(status().isBadRequest());

        mockMvc.perform(put("/directors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "id", 9999,
                                "name", "Неизвестный"))))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnFilmsByDirectorSortedByYearAndLikes() throws Exception {
        long directorId = createDirector("Режиссёр");
        long olderFilmId = createFilm("Старый фильм", "1999-04-30", directorId);
        long newerFilmId = createFilm("Новый фильм", "2005-04-30", directorId);
        long userId = createUser();

        mockMvc.perform(put("/films/{id}/like/{userId}", newerFilmId, userId))
                .andExpect(status().isOk());

        mockMvc.perform(get("/films/director/{directorId}", directorId).param("sortBy", "year"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(olderFilmId))
                .andExpect(jsonPath("$[1].id").value(newerFilmId))
                .andExpect(jsonPath("$[0].directors[0].id").value(directorId));

        mockMvc.perform(get("/films/director/{directorId}", directorId).param("sortBy", "likes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(newerFilmId))
                .andExpect(jsonPath("$[1].id").value(olderFilmId));
    }

    @Test
    void shouldRemoveDirectorFromFilmWhenDirectorDeleted() throws Exception {
        long directorId = createDirector("Временный режиссёр");
        long filmId = createFilm("Фильм", "2000-01-01", directorId);

        mockMvc.perform(delete("/directors/{id}", directorId))
                .andExpect(status().isOk());

        mockMvc.perform(get("/films/{id}", filmId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.directors").isEmpty());

        mockMvc.perform(get("/films/director/{directorId}", directorId).param("sortBy", "likes"))
                .andExpect(status().isNotFound());
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

    private long createFilm(String name, String releaseDate, long directorId) throws Exception {
        String response = mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "name", name,
                                "description", "Описание",
                                "releaseDate", releaseDate,
                                "duration", 120,
                                "mpa", Map.of("id", 3),
                                "directors", List.of(Map.of("id", directorId))))))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return objectMapper.readTree(response).get("id").asLong();
    }

    private long createUser() throws Exception {
        String response = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "email", "director-test@mail.ru",
                                "login", "director-test",
                                "name", "Пользователь",
                                "birthday", "1990-01-01"))))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return objectMapper.readTree(response).get("id").asLong();
    }
}
