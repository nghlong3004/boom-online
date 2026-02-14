CREATE TABLE exchange_token
(
    id          BIGSERIAL PRIMARY KEY,
    token       VARCHAR(255)             NOT NULL UNIQUE,
    email       VARCHAR(255)             NOT NULL,
    expiry_time TIMESTAMP WITH TIME ZONE NOT NULL
);