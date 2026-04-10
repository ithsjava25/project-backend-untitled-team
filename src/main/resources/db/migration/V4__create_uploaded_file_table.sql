CREATE TABLE uploaded_file
(
    id                        BIGSERIAL PRIMARY KEY,
    filename                  VARCHAR(255) NOT NULL,
    s3key                     VARCHAR(255) NOT NULL,
    uploaded_at               TIMESTAMP    NOT NULL,
    uploaded_by_id            BIGINT       NOT NULL REFERENCES users (id),
    associated_case_entity_id BIGINT       NOT NULL REFERENCES case_entity (id)
);
