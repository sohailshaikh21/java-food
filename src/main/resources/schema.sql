CREATE TABLE IF NOT EXISTS users (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    name             VARCHAR(100) NOT NULL,
    email            VARCHAR(150) NOT NULL UNIQUE,
    password_hash    VARCHAR(100) NOT NULL,
    role             VARCHAR(20)  NOT NULL,
    auth_token       VARCHAR(100),
    token_expires_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS menu_items (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(120) NOT NULL,
    category    VARCHAR(60)  NOT NULL,
    price       INT          NOT NULL,
    food_type   VARCHAR(20)  NOT NULL,
    image_url   VARCHAR(500),
    description VARCHAR(400),
    available   BOOLEAN      NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS orders (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT,
    customer_name   VARCHAR(100) NOT NULL,
    phone           VARCHAR(20)  NOT NULL,
    address         VARCHAR(255),
    payment_method  VARCHAR(50),
    delivery_charge INT          NOT NULL DEFAULT 0,
    total_amount    INT          NOT NULL DEFAULT 0,
    status          VARCHAR(30)  NOT NULL,
    order_date      TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_orders_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS order_items (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id     BIGINT       NOT NULL,
    menu_item_id BIGINT       NOT NULL,
    item_name    VARCHAR(120) NOT NULL,
    unit_price   INT          NOT NULL,
    quantity     INT          NOT NULL,
    CONSTRAINT fk_items_order FOREIGN KEY (order_id) REFERENCES orders (id)
);

CREATE TABLE IF NOT EXISTS reservations (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_name    VARCHAR(100) NOT NULL,
    phone            VARCHAR(20)  NOT NULL,
    reservation_date DATE         NOT NULL,
    reservation_time TIME         NOT NULL,
    guests           INT          NOT NULL
);

