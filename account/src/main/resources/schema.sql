CREATE TABLE IF NOT EXISTS account (
    id      BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    balance BIGINT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT chk_account_balance CHECK (balance >= 0)
);

CREATE TABLE IF NOT EXISTS account_transaction (
    id         BIGINT      NOT NULL AUTO_INCREMENT,
    order_id   BIGINT      NOT NULL,
    account_id BIGINT      NOT NULL,
    type       VARCHAR(20) NOT NULL,
    amount     BIGINT      NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_account_transaction_order_type UNIQUE (order_id, type)
);
