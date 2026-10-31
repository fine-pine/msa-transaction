CREATE TABLE IF NOT EXISTS product (
    id       BIGINT       NOT NULL AUTO_INCREMENT,
    name     VARCHAR(100) NOT NULL,
    price    BIGINT       NOT NULL,
    quantity INT          NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT chk_product_price CHECK (price > 0),
    CONSTRAINT chk_product_quantity CHECK (quantity >= 0)
);

CREATE TABLE IF NOT EXISTS orders (
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    product_id BIGINT       NOT NULL,
    quantity   INT          NOT NULL,
    account_id BIGINT       NOT NULL,
    amount     BIGINT       NOT NULL,
    address    VARCHAR(255) NOT NULL,
    status     VARCHAR(20)  NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS outbox (
    id       BIGINT      NOT NULL AUTO_INCREMENT,
    topic    VARCHAR(50) NOT NULL,
    order_id BIGINT      NOT NULL,
    type     VARCHAR(20) NOT NULL,
    payload  JSON        NOT NULL,
    PRIMARY KEY (id)
);
