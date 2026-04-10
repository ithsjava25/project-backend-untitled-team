CREATE TABLE comment
(
    id             BIGSERIAL PRIMARY KEY,
    text           TEXT      NOT NULL,
    created_at     TIMESTAMP NOT NULL,
    author_id      BIGINT    NOT NULL REFERENCES users (id),
    case_entity_id BIGINT    NOT NULL REFERENCES case_entity (id)
);
