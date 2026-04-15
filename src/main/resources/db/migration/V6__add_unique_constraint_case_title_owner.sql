ALTER TABLE case_entity
    ADD CONSTRAINT uq_case_entity_title_owner UNIQUE (title, owner_id);
