create table PRODUCTS
(
    id        BIGINT AUTO_INCREMENT,
    name      VARCHAR(100) UNIQUE,
    price     DECIMAL(10, 2),
    image_url VARCHAR(500),
    PRIMARY KEY (id)
);

create table MEMBERS
(
    id        BIGINT AUTO_INCREMENT,
    email     VARCHAR(20) UNIQUE,
    name      VARCHAR(100) DEFAULT '',
    password  VARCHAR(50),
    role      VARCHAR(10),
    PRIMARY KEY (id)
);

create table CARTS
(
    cart_id        BIGINT AUTO_INCREMENT,
    user_id   BIGINT UNIQUE,
    PRIMARY KEY (cart_id),
    FOREIGN KEY (user_id) REFERENCES MEMBERS(id)
);

create table CART_ITEMS (
    cart_id BIGINT,
    product_id BIGINT,
    quantity INT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (cart_id, product_id),
    FOREIGN KEY (cart_id) REFERENCES CARTS(cart_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES PRODUCTS(id)
);
