create table PRODUCTS
(
    name      VARCHAR(100) UNIQUE,
    price     DECIMAL(10, 2),
    image_url VARCHAR(500),
    id        BIGINT AUTO_INCREMENT UNIQUE,
    PRIMARY KEY (id)
);

create table MEMBERS
(
    email     VARCHAR(50) UNIQUE,
    name      VARCHAR(100) DEFAULT '',
    password  VARCHAR(50),
    role      VARCHAR(10),
    id        BIGINT AUTO_INCREMENT UNIQUE,
    PRIMARY KEY (id)
);

create table CARTS
(
    member_id BIGINT,
    id        BIGINT AUTO_INCREMENT UNIQUE,
    FOREIGN KEY (member_id) REFERENCES MEMBERS(id)
);

create table CART_ITEMS (
    cart_id     BIGINT,
    product_id  BIGINT,
    quantity    INT DEFAULT 1,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    id          BIGINT AUTO_INCREMENT UNIQUE,
    PRIMARY KEY (id),
    FOREIGN KEY (product_id) REFERENCES PRODUCTS(id),
    FOREIGN KEY (cart_id) REFERENCES CARTS(id)

);