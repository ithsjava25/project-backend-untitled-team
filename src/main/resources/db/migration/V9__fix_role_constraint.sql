ALTER TABLE users
DROP CONSTRAINT users_role_check,
ADD CONSTRAINT users_role_check
  CHECK (role IN ('USER', 'HANDLER', 'SUPERVISOR', 'ADMIN'));
