# java-filmorate

Хранит данные в БД Postgresql.

### Стек:
- Java 11 (Core, Collections, Optional)
- Spring Boot
- JDBC
- Postgresql database
- Maven
- Lombok
- Junit5
- Postman

### Цели приложения:
В этом проекте отрабатывалось практическое применение:
* фреймворка Spring Boot
* системы сборки Maven
* библиотеки логирования Lombok
* архитектуры REST
* Dependency Injection
* написание SQL-запросов и работа с БД через JDBC
* командная разработка, merge веток и устранение конфликтов слияния

### Описание приложения:
Данный проект представляет собой бекенд веб-сервиса для работы с фильмами, пользователями и отзывами, а также для управления связями между ними. Основной целью этого проекта является предоставление пользователю возможности удобно и эффективно управлять информацией о фильмах, обмениваться отзывами и рекомендациями с другими пользователями, а также находить интересные фильмы на основе их предпочтений.

Сервис позволяет пользователям наслаждаться фильмами, обмениваться мнениями и рекомендациями, находить новые интересные фильмы и узнавать о последних событиях в кругу своих друзей.

### Структура приложения:

1. Пакет 'controller': Здесь размещаются классы контроллеров, которые обрабатывают HTTP-запросы и управляют взаимодействием с клиентом.

2. Пакет 'service': В этом пакете содержатся интерфейсы и классы сервисов, которые реализуют бизнес-логику приложения.

3. Пакет 'repository': Здесь размещаются интерфейсы и классы репозиториев, которые предоставляют абстракцию для доступа к базе данных.

4. Пакет 'model': В этом пакете определяются классы приложения. Они представляют данные, с которыми работает приложение.

5. Пакет 'exception': В этом пакете определены классы исключений, которые обрабатываются в приложении.

6. Пакет 'util': В этом пакете находятся самописные аннотации для валидации данных.

### Реализованы следующие эндпоинты:

#### 1. Фильмы
+ POST /films - создание фильма

+ PUT /films - редактирование фильма

+ GET /films - получение списка всех фильмов

+ GET /films/{id} - получение информации о фильме по его id

+ PUT /films/{id}/like/{userId} — поставить лайк фильму

+ DELETE /films/{id}/like/{userId} — удалить лайк фильма

+ DELETE /films/{id} - удаление фильма по id

+ GET /films/popular?count={count} — возвращает список из первых count фильмов по количеству лайков. Если значение параметра count не задано, возвращает первые 10.

+ GET /films/search?query={query}?by={by} - поиск фильмов по заголовку и режиссеру

+ GET /films/director/directorId={directorId}?sortBy={sortBy} - получение всех фильмов режиссера с сортировкой по лайкам или годам

+ GET /films/common?userId={userId}?friendId={friendId} - получение общих фильмов пользователя и его друга

#### 2. Пользователи

+ POST /users - создание пользователя

+ PUT /users - редактирование пользователя

+ GET /users - получение списка всех пользователей

+ DELETE /users/{userId} - удаление пользователя по id

+ GET /users/{id} - получение данных о пользователе по id

+ PUT /users/{id}/friends/{friendId} — добавление в друзья

+ DELETE /users/{id}/friends/{friendId} — удаление из друзей

+ GET /users/{id}/friends — возвращает список друзей

+ GET /users/{id}/friends/common/{otherId} — возвращает список друзей, общих с другим пользователем

+ GET /users/{id}/recommendations - получение рекомендаций по фильмам

+ GET /users/{id}/feed - возвращает ленту событий пользователя

#### 3. Режиссеры

+ POST /directors - создание режиссера

+ GET /directors - получение списка всех режиссеров

+ GET /directors/{id} - получение режиссера по id

+ PUT /directors - обновление режиссера

+ DELETE /directors/{id} - удаление режиссера по id

#### 4. Жанры

+ GET /genres - получение списка всех жанров

+ GET /genres/{id} - получение жанра по id

#### 5. MPA рейтинг

+ GET /mpa - получение списка всех рейтингов

+ GET /mpa/{id} - получение рейтинга по id

#### 6. Отзывы

+ POST /reviews - создание отзыва

+ PUT /reviews - обновление отзыва

+ DELETE /reviews/{id} - удаление отзыва по id

+ GET /reviews/{id} - получение отзыва по id

+ GET /reviews?filmId={filmId}?count={count} - получение count отзывов по id фильма. Если значение не задано, возвращает первые 10

#### 7. Лайки

+ PUT /reviews/{id}/like/{userId} - добавление лайка

+ PUT /reviews/{id}/dislike/{userId} - добавление дизлайка

+ DELETE /reviews/{id}/like{userId} - удаление лайка

+ DELETE /reviews{id}/dislike{userId} - удаление дизлайка




Схема базы данных:
![filmorate.png](/src/main/resources/diagramma/filmorate.png)

#### Примеры запросов:




1. Пользователи


создание пользователя

 ```sql
 INSERT INTO users (name, email, login, birthday)
 VALUES ( ?, ?, ?, ? );
 ```

редактирование пользователя

 ```sql
 UPDATE users
 SET email = ?,
 login = ?,
 name = ?,
 birthday = ?
 WHERE user_id = ?;
 ```

получение списка всех пользователей

 ```sql
 SELECT *
 FROM users;
 ```
получение информации о пользователе по его id

 ```sql
 SELECT *
 FROM users
 WHERE user_id = ?;
 ```

добавление в друзья

 ```sql
INSERT INTO friend_request(initiator_user_id, target_user_id, confirmed)
SELECT ?, ?, false
WHERE NOT EXISTS(SELECT 1
                  FROM friend_request
                  WHERE initiator_user_id = ? AND target_user_id = ?);
 ```

удаление из друзей

 ```sql
 DELETE
 FROM friend_request
 WHERE initiator_user_id = ? AND target_user_id = ?;
 ```

возвращает список пользователей, являющихся его друзьями
 ```sql
SELECT u.* 
FROM users u 
WHERE u.user_id in(SELECT fr.target_user_id
                    FROM friend_request fr 
                    WHERE fr.initiator_user_id = ? 
                    UNION
                    SELECT fr.initiator_user_id
                    FROM friend_request fr
                    WHERE fr.target_user_id = ? AND fr.confirmed = true);
 ```

2. Фильмы

создание фильма

 ```sql
 INSERT INTO film (name, description, release_date, duration, rating_id)
 VALUES (?, ?, ?, ?, ?);
 ```

редактирование фильма

 ```sql
UPDATE film SET 
name = ?, 
description = ?, 
release_date = ?, 
duration = ?, 
rating_id = ? 
WHERE film_id = ?;
 ```

получение списка всех фильмов

 ```sql
SELECT *
FROM film f
INNER JOIN mpa_rating mr ON (f.rating_id = mr.rating_id)
ORDER BY film_id;
 ```

получение информации о фильме по его id

 ```sql
SELECT * FROM film f
INNER JOIN mpa_rating m
ON (f.rating_id = m.rating_id)
WHERE f.film_id=?;
 ```

пользователь ставит лайк фильму

  ```sql
INSERT INTO likes(film_id, user_id)
SELECT ?, ?
WHERE NOT EXISTS (SELECT 1
                    FROM likes
                    WHERE film_id = ? AND user_id = ?);
  ```

пользователь удаляет лайк

  ```sql
DELETE FROM likes
WHERE film_id = ? AND user_id = ?;
  ```

возвращает топ n популярных фильмов
 ```sql
SELECT f.*, mr.rating_name
FROM film f
INNER JOIN mpa_rating mr USING (rating_id)
LEFT OUTER JOIN likes l USING (film_id)
GROUP BY f.film_id, mr.rating_name
ORDER BY COUNT(l.user_id) DESC , f.film_id
LIMIT ?;
 ```
 
 