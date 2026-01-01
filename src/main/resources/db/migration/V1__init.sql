CREATE TABLE payments
(
    id                 BIGINT AUTO_INCREMENT NOT NULL,
    order_id           BIGINT         NOT NULL,
    user_email         VARCHAR(255)   NOT NULL,
    amount             DECIMAL(19, 2) NOT NULL,
    status             VARCHAR(255)   NOT NULL,
    payment_gateway    VARCHAR(20)    NOT NULL,
    gateway_payment_id VARCHAR(100)   NOT NULL,
    payment_link       VARCHAR(255) NULL,
    created_at         datetime NULL,
    updated_at         datetime NULL,
    CONSTRAINT pk_payments PRIMARY KEY (id)
);

ALTER TABLE payments
    ADD CONSTRAINT uk_gateway_payment_id UNIQUE (gateway_payment_id);

CREATE INDEX idx_gateway_payment_id ON payments (gateway_payment_id);

CREATE INDEX idx_order_id ON payments (order_id);