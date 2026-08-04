package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ReviewControllerTest {

    private static final int UNKNOWN_ID = 999999;
    private static final AtomicInteger COUNTER = new AtomicInteger();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateGetUpdateAndDeleteReview() throws Exception {
        long userId = createUser();
        long filmId = createFilm();

        long reviewId = createReview("Отличный фильм", true, userId, filmId);

        mockMvc.perform(get("/reviews/{id}", reviewId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reviewId").value(reviewId))
                .andExpect(jsonPath("$.content").value("Отличный фильм"))
                .andExpect(jsonPath("$.isPositive").value(true))
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.filmId").value(filmId))
                .andExpect(jsonPath("$.useful").value(0));

        mockMvc.perform(put("/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJson(reviewBody(reviewId, "Обновлённый отзыв", false, userId, filmId))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reviewId").value(reviewId))
                .andExpect(jsonPath("$.content").value("Обновлённый отзыв"))
                .andExpect(jsonPath("$.isPositive").value(false));

        mockMvc.perform(delete("/reviews/{id}", reviewId))
                .andExpect(status().isOk());

        mockMvc.perform(get("/reviews/{id}", reviewId))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldChangeUsefulOnLikeAndDislike() throws Exception {
        long authorId = createUser();
        long likerId = createUser();
        long dislikerId = createUser();
        long filmId = createFilm();
        long reviewId = createReview("Контент", true, authorId, filmId);

        mockMvc.perform(put("/reviews/{id}/like/{userId}", reviewId, likerId))
                .andExpect(status().isOk());

        mockMvc.perform(get("/reviews/{id}", reviewId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.useful").value(1));

        mockMvc.perform(put("/reviews/{id}/dislike/{userId}", reviewId, dislikerId))
                .andExpect(status().isOk());

        mockMvc.perform(get("/reviews/{id}", reviewId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.useful").value(0));

        mockMvc.perform(delete("/reviews/{id}/like/{userId}", reviewId, likerId))
                .andExpect(status().isOk());

        mockMvc.perform(get("/reviews/{id}", reviewId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.useful").value(-1));

        mockMvc.perform(delete("/reviews/{id}/dislike/{userId}", reviewId, dislikerId))
                .andExpect(status().isOk());

        mockMvc.perform(get("/reviews/{id}", reviewId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.useful").value(0));
    }

    @Test
    void shouldReturnReviewsSortedByUsefulDesc() throws Exception {
        long filmId = createFilm();
        long author1 = createUser();
        long author2 = createUser();
        long author3 = createUser();
        long voter1 = createUser();
        long voter2 = createUser();

        long lowUsefulId = createReview("Низкий useful", true, author1, filmId);
        long midUsefulId = createReview("Средний useful", true, author2, filmId);
        long highUsefulId = createReview("Высокий useful", true, author3, filmId);

        mockMvc.perform(put("/reviews/{id}/like/{userId}", highUsefulId, voter1))
                .andExpect(status().isOk());
        mockMvc.perform(put("/reviews/{id}/like/{userId}", highUsefulId, voter2))
                .andExpect(status().isOk());
        mockMvc.perform(put("/reviews/{id}/like/{userId}", midUsefulId, voter1))
                .andExpect(status().isOk());
        mockMvc.perform(put("/reviews/{id}/dislike/{userId}", lowUsefulId, voter1))
                .andExpect(status().isOk());

        mockMvc.perform(get("/reviews").param("filmId", String.valueOf(filmId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].reviewId").value(highUsefulId))
                .andExpect(jsonPath("$[1].reviewId").value(midUsefulId))
                .andExpect(jsonPath("$[2].reviewId").value(lowUsefulId));
    }

    @Test
    void shouldFilterReviewsByFilmId() throws Exception {
        long film1 = createFilm();
        long film2 = createFilm();
        long user1 = createUser();
        long user2 = createUser();
        long user3 = createUser();

        long reviewFilm1 = createReview("Про фильм 1", true, user1, film1);
        createReview("Про фильм 2", true, user2, film2);
        createReview("Ещё про фильм 2", false, user3, film2);

        mockMvc.perform(get("/reviews").param("filmId", String.valueOf(film1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].reviewId").value(reviewFilm1))
                .andExpect(jsonPath("$[0].filmId").value(film1));
    }

    @Test
    void shouldUseDefaultCountOfTen() throws Exception {
        long filmId = createFilm();

        for (int i = 0; i < 12; i++) {
            long userId = createUser();
            createReview("Отзыв " + i, true, userId, filmId);
        }

        mockMvc.perform(get("/reviews").param("filmId", String.valueOf(filmId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(10)));

        mockMvc.perform(get("/reviews")
                        .param("filmId", String.valueOf(filmId))
                        .param("count", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    void shouldReturn404ForUnknownIds() throws Exception {
        long userId = createUser();
        long filmId = createFilm();

        mockMvc.perform(get("/reviews/{id}", UNKNOWN_ID))
                .andExpect(status().isNotFound());

        mockMvc.perform(put("/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJson(reviewBody((long) UNKNOWN_ID, "Нет такого", true, userId, filmId))))
                .andExpect(status().isNotFound());

        mockMvc.perform(delete("/reviews/{id}", UNKNOWN_ID))
                .andExpect(status().isNotFound());

        mockMvc.perform(post("/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJson(reviewBody(null, "Нет пользователя", true, UNKNOWN_ID, filmId))))
                .andExpect(status().isNotFound());

        mockMvc.perform(post("/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJson(reviewBody(null, "Нет фильма", true, userId, UNKNOWN_ID))))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/reviews").param("filmId", String.valueOf(UNKNOWN_ID)))
                .andExpect(status().isNotFound());

        long reviewId = createReview("Есть отзыв", true, userId, filmId);

        mockMvc.perform(put("/reviews/{id}/like/{userId}", reviewId, UNKNOWN_ID))
                .andExpect(status().isNotFound());

        mockMvc.perform(put("/reviews/{id}/dislike/{userId}", UNKNOWN_ID, userId))
                .andExpect(status().isNotFound());
    }

    private Map<String, Object> reviewBody(Long reviewId, String content, boolean isPositive,
                                           long userId, long filmId) {
        Map<String, Object> review = new LinkedHashMap<>();
        if (reviewId != null) {
            review.put("reviewId", reviewId);
        }
        review.put("content", content);
        review.put("isPositive", isPositive);
        review.put("userId", userId);
        review.put("filmId", filmId);
        return review;
    }

    private String asJson(Object body) throws Exception {
        return objectMapper.writeValueAsString(body);
    }

    private long createUser() throws Exception {
        String login = "review-user-" + COUNTER.incrementAndGet() + "-" + System.nanoTime();
        String response = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJson(Map.of(
                                "email", login + "@mail.ru",
                                "login", login,
                                "name", "Пользователь",
                                "birthday", "1990-01-01"))))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return objectMapper.readTree(response).get("id").asLong();
    }

    private long createFilm() throws Exception {
        String response = mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJson(Map.of(
                                "name", "Фильм " + COUNTER.incrementAndGet(),
                                "description", "Описание",
                                "releaseDate", "2000-01-01",
                                "duration", 120,
                                "mpa", Map.of("id", 3)))))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return objectMapper.readTree(response).get("id").asLong();
    }

    private long createReview(String content, boolean isPositive, long userId, long filmId)
            throws Exception {
        String response = mockMvc.perform(post("/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJson(reviewBody(null, content, isPositive, userId, filmId))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reviewId").isNumber())
                .andExpect(jsonPath("$.useful").value(0))
                .andReturn()
                .getResponse()
                .getContentAsString();
        return objectMapper.readTree(response).get("reviewId").asLong();
    }
}
