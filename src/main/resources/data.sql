DELETE FROM film_genres;
DELETE FROM likes;
DELETE FROM films;
DELETE FROM users;
DELETE FROM genres;
DELETE FROM mpa;

ALTER TABLE films ALTER COLUMN film_id RESTART WITH 1;
ALTER TABLE users ALTER COLUMN user_id RESTART WITH 1;

MERGE INTO mpa (mpa_id, name) KEY (mpa_id) VALUES
(1, 'G'),
(2, 'PG'),
(3, 'PG-13'),
(4, 'R'),
(5, 'NC-17');

MERGE INTO genres (genre_id, name) KEY (genre_id) VALUES
(1, 'Комедия'),
(2, 'Драма'),
(3, 'Мультфильм'),
(4, 'Триллер'),
(5, 'Документальный'),
(6, 'Боевик');

INSERT INTO users (user_id, email, login, name, birthday) VALUES
(1, 'user1@mail.ru', 'user1', 'User One', '1990-01-01'),
(2, 'user2@mail.ru', 'user2', 'User Two', '1995-05-05'),
(3, 'user3@mail.ru', 'user3', 'User Three', '2000-10-10');

INSERT INTO films (film_id, name, description, release_date, duration, mpa_id) VALUES
(1, 'Old Film', 'Old film description', '1989-04-17', 190, 5),
(2, 'New film', 'New film about friends', '1999-04-30', 120, 3),
(3, 'New film with director', 'Film with director', '1999-04-30', 120, 3);

INSERT INTO film_genres (film_id, genre_id) VALUES
(1, 1),
(2, 1),
(2, 2),
(3, 1);

INSERT INTO likes (film_id, user_id) VALUES
(2, 1),
(2, 2),
(2, 3),
(3, 1),
(3, 2);