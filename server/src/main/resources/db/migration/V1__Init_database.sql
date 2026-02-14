CREATE TYPE role AS ENUM ('USER', 'ADMIN');
CREATE TABLE bomber
(
    id            BIGSERIAL PRIMARY KEY,
    email         VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255)        NOT NULL,
    display_name     VARCHAR(12)         NOT NULL,
    birthday      VARCHAR(30)         NOT NULL,
    role          role,
    gender        VARCHAR(1)          NOT NULL,
    created       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated       TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);