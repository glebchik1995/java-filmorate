CREATE TABLE IF NOT EXISTS ratings_mpa (
    ratings_mpa_id INTEGER auto_increment
        primary key,
    mpa_name VARCHAR(40) NOT NULL,
    description VARCHAR(200) NOT NULL
);

CREATE TABLE IF NOT EXISTS genres (
    genre_id INTEGER auto_increment
        primary key,
    genre_name VARCHAR(40) NOT NULL
);

CREATE TABLE IF NOT EXISTS films (
    film_id INTEGER auto_increment
        primary key,
    name VARCHAR(30) NOT NULL,
    description VARCHAR(400),
    release_date TIMESTAMP NOT NULL,
    duration INTEGER NOT NULL,
    ratings_mpa_id INTEGER NOT NULL REFERENCES ratings_mpa (ratings_mpa_id)
);

CREATE TABLE IF NOT EXISTS film_genres (
    film_id  INTEGER REFERENCES films (film_id) ON DELETE CASCADE,
    genre_id INTEGER REFERENCES genres (genre_id) ON DELETE CASCADE,
    PRIMARY KEY (film_id, genre_id)
);

CREATE TABLE IF NOT EXISTS users (
    user_id INTEGER auto_increment
        primary key,
    email VARCHAR(40) NOT NULL,
    login VARCHAR(30) NOT NULL,
    name VARCHAR(30) NOT NULL,
    birthday TIMESTAMP
);

CREATE TABLE IF NOT EXISTS friends (
    user_id INTEGER NOT NULL REFERENCES users (user_id),
    other_user_id INTEGER NOT NULL REFERENCES users (user_id),
    status BOOLEAN
);

CREATE TABLE IF NOT EXISTS likes (
    film_id INTEGER NOT NULL REFERENCES films (film_id),
    user_id INTEGER NOT NULL REFERENCES users (user_id),
    PRIMARY KEY (film_id, user_id)
);
