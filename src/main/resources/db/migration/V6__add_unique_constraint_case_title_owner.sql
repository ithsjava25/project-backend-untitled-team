DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM case_entity
        GROUP BY title, owner_id
        HAVING COUNT(*) > 1
    ) THEN
        RAISE EXCEPTION 'Cannot add uq_case_entity_title_owner: duplicate (title, owner_id) rows exist in case_entity';
    END IF;
END $$;


ALTER TABLE case_entity
    ADD CONSTRAINT uq_case_entity_title_owner UNIQUE (title, owner_id);
