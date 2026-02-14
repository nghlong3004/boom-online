CREATE TYPE auth_provider AS ENUM ('LOCAL', 'GOOGLE');
ALTER TABLE bomber
    ADD COLUMN google_id     VARCHAR(255) UNIQUE,
    ADD COLUMN auth_provider auth_provider DEFAULT 'LOCAL';

UPDATE bomber
SET auth_provider = 'LOCAL'
WHERE auth_provider IS NULL;
