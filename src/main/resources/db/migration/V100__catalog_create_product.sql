CREATE TABLE catalog.products (
    id UUID PRIMARY KEY,
    sku VARCHAR(40) NOT NULL UNIQUE,
    name VARCHAR(200) NOT NULL,
    price NUMERIC(12, 2) NOT NULL CHECK (price > 0),
    stock INT NOT NULL CHECK ( stock >= 0 ),
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL default now()
);