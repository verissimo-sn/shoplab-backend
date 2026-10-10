CREATE TABLE orders.customer_orders (
id                  UUID PRIMARY KEY,
customer_email      VARCHAR(200)   NOT NULL,
zip_code            VARCHAR(8)     NOT NULL,
shipping_cost       NUMERIC(12, 2) NOT NULL,
total               NUMERIC(12, 2) NOT NULL,
status              VARCHAR(20)    NOT NULL,
cancellation_reason VARCHAR(300),
version             BIGINT         NOT NULL DEFAULT 0,
created_at          TIMESTAMPTZ    NOT NULL DEFAULT now()
);

CREATE TABLE orders.order_items (
id            UUID PRIMARY KEY,
order_id      UUID           NOT NULL REFERENCES orders.customer_orders (id),
product_id    UUID           NOT NULL, -- sem FK para catalog: fronteira de módulo
sku           VARCHAR(40)    NOT NULL,
product_name  VARCHAR(200)   NOT NULL,
unit_price    NUMERIC(12, 2) NOT NULL,
quantity      INT            NOT NULL CHECK (quantity > 0)
);

CREATE INDEX idx_order_items_order ON orders.order_items (order_id);