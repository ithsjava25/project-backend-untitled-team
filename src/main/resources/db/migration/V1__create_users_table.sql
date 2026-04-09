CREATE TABLE users
(
    id         BIGSERIAL PRIMARY KEY,
    username   VARCHAR(255) NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    email      VARCHAR(255) UNIQUE,
    role       VARCHAR(50)  NOT NULL,
    created_at TIMESTAMP    NOT NULL
);
