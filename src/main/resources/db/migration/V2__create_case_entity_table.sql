CREATE TABLE case_entity
(
    id             BIGSERIAL PRIMARY KEY,
    title          VARCHAR(255) NOT NULL,
    description    TEXT,
    status         VARCHAR(50)  NOT NULL,
    owner_id       BIGINT       NOT NULL REFERENCES users (id),
    assigned_to_id BIGINT REFERENCES users (id),
    created_at     TIMESTAMP    NOT NULL
);

CREATE INDEX idx_case_entity_owner_id ON case_entity (owner_id);
CREATE INDEX idx_case_entity_assigned_to_id ON case_entity (assigned_to_id);
