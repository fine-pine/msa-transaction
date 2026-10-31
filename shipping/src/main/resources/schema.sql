CREATE TABLE IF NOT EXISTS shipment (
    id       BIGINT       NOT NULL AUTO_INCREMENT,
    order_id BIGINT       NOT NULL,
    address  VARCHAR(255) NOT NULL,
    status   VARCHAR(20)  NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_shipment_order UNIQUE (order_id)
);

CREATE TABLE IF NOT EXISTS outbox (
    id       BIGINT      NOT NULL AUTO_INCREMENT,
    topic    VARCHAR(50) NOT NULL,
    order_id BIGINT      NOT NULL,
    type     VARCHAR(20) NOT NULL,
    payload  JSON        NOT NULL,
    PRIMARY KEY (id)
);
