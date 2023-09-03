DROP TABLE IF EXISTS hit;

CREATE TABLE IF NOT EXISTS hit
(
    id
    BIGINT
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    PRIMARY
    KEY
    NOT
    NULL,
    app
    VARCHAR
(
    255
) NOT NULL,
    uri VARCHAR
(
    64
) NOT NULL,
    ip VARCHAR
(
    16
) NOT NULL,
    timestamp TIMESTAMP WITHOUT TIME ZONE not null
    );