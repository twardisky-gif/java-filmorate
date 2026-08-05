# java-filmorate

Сервис для работы с фильмами и оценками пользователей. Хранит фильмы, пользователей, лайки и дружеские связи в базе данных H2.

## Схема базы данных

```mermaid
erDiagram
    mpa ||--o{ films : "имеет"
    films ||--o{ film_genres : "содержит"
    genres ||--o{ film_genres : "относится"
    films ||--o{ likes : "получает"
    users ||--o{ likes : "ставит"
    users ||--o{ friendships : "добавляет"
    users ||--o{ reviews : "пишет"
    films ||--o{ reviews : "имеет"
    reviews ||--o{ review_reactions : "получает"
    users ||--o{ review_reactions : "ставит"
    users ||--o{ events : "produces"

    mpa {
        integer mpa_id PK
        varchar name UK
    }
    genres {
        integer genre_id PK
        varchar name UK
    }
    users {
        bigint user_id PK
        varchar email UK
        varchar login
        varchar name
        date birthday
    }
    films {
        bigint film_id PK
        varchar name
        varchar description
        date release_date
        integer duration
        integer mpa_id FK
    }
    film_genres {
        bigint film_id PK_FK
        integer genre_id PK_FK
    }
    likes {
        bigint film_id PK_FK
        bigint user_id PK_FK
    }
    friendships {
        bigint user_id PK_FK
        bigint friend_id PK_FK
        boolean status
    }
    reviews {
        bigint review_id PK
        varchar content
        boolean is_positive
        bigint user_id FK
        bigint film_id FK
    }
    review_reactions {
        bigint review_id PK_FK
        bigint user_id PK_FK
        boolean is_like
    }
    events {
        bigint event_id PK
        bigint user_id FK
        varchar event_type
        varchar operation
        bigint entity_id
        bigint event_timestamp
    }
```

Пояснения к схеме:

- `mpa` и `genres` это справочники. Рейтинг у фильма один, жанров может быть несколько.
- `film_genres` и `likes` связывают таблицы многие ко многим. Составной первичный ключ не даёт продублировать жанр у фильма и второй лайк от того же пользователя.
- `friendships` хранит одностороннюю дружбу: строка означает, что `user_id` добавил `friend_id` к себе в друзья. Поле `status` отражает подтверждение заявки.
- `reviews` — один отзыв на пару `(user_id, film_id)` (ограничение `uq_reviews_user_film`). `review_reactions` хранит лайк или дизлайк отзыва (`is_like`); полезность отзыва считается как лайки минус дизлайки и в таблице не хранится.
- `events` — лента действий пользователя (лайки, дружба, отзывы): тип (`LIKE`, `FRIEND`, `REVIEW`), операция (`ADD`, `UPDATE`, `REMOVE`), идентификатор сущности и метка времени.

## API пользователей

- `GET /users/{id}/feed` — лента событий пользователя

## Примеры запросов

Получить топ 10 фильмов по количеству лайков:

```sql
SELECT f.film_id, f.name, m.name AS mpa_name
FROM films f
JOIN mpa m ON m.mpa_id = f.mpa_id
LEFT JOIN likes l ON l.film_id = f.film_id
GROUP BY f.film_id, f.name, m.name
ORDER BY COUNT(l.user_id) DESC, f.film_id
LIMIT 10;
```

Найти общих друзей двух пользователей:

```sql
SELECT u.user_id, u.login
FROM friendships f1
JOIN friendships f2 ON f1.friend_id = f2.friend_id
JOIN users u ON u.user_id = f1.friend_id
WHERE f1.user_id = ? AND f2.user_id = ?;
```

Получить жанры конкретного фильма:

```sql
SELECT g.genre_id, g.name
FROM film_genres fg
JOIN genres g ON g.genre_id = fg.genre_id
WHERE fg.film_id = ?
ORDER BY g.genre_id;
```

## Запуск

```
mvn clean package
java -jar target/filmorate-0.0.1-SNAPSHOT.jar
```

Приложение поднимается на порту 8080, файл базы данных создаётся в каталоге `db`.
