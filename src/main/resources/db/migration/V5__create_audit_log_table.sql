CREATE TABLE audit_log
(
    id             BIGSERIAL PRIMARY KEY,
    action         VARCHAR(50) NOT NULL,
    timestamp      TIMESTAMP   NOT NULL,
    user_id        BIGINT REFERENCES users (id),
    case_entity_id BIGINT      NOT NULL REFERENCES case_entity (id)
);
