ALTER TABLE audit_log
DROP CONSTRAINT IF EXISTS audit_log_user_id_fkey,
    DROP CONSTRAINT IF EXISTS audit_log_case_entity_id_fkey;

ALTER TABLE audit_log
    RENAME COLUMN case_entity_id TO case_id;

ALTER TABLE audit_log
    ALTER COLUMN case_id DROP NOT NULL;
