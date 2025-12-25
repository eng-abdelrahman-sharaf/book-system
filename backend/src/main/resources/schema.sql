create type role_enum as ENUM('Admin', 'Customer');
CREATE TABLE IF NOT EXISTS Users
(
    user_id          INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    username         VARCHAR(50)  NOT NULL UNIQUE,
    password         VARCHAR(255) NOT NULL,
    first_name       VARCHAR(50)  NOT NULL,
    last_name        VARCHAR(50)  NOT NULL,
    email            VARCHAR(100) NOT NULL UNIQUE,
    phone            VARCHAR(20),
    shipping_address VARCHAR(255),
    role             role_enum    NOT NULL,
    created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS Publishers
(
    publisher_id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name         VARCHAR(100) NOT NULL,
    address      VARCHAR(255),
    phone        VARCHAR(20)
);
create type category_type as ENUM('Science', 'Art', 'Religion', 'History', 'Geography');
CREATE TABLE IF NOT EXISTS Books
(
    isbn            VARCHAR(20) PRIMARY KEY,
    title           VARCHAR(255)   NOT NULL,
    publisher_id    INT            NOT NULL,
    publication_year INT,
    selling_price   DECIMAL(10, 2) NOT NULL,
    category        category_type  NOT NULL,
    number_of_books INT DEFAULT 0,
    threshold       INT DEFAULT 5,
    FOREIGN KEY (publisher_id) REFERENCES Publishers (publisher_id)
);
CREATE TABLE IF NOT EXISTS Authors
(
    author_id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name      VARCHAR(100) NOT NULL
);
CREATE TABLE IF NOT EXISTS BookAuthors
(
    isbn      VARCHAR(20),
    author_id INT,
    PRIMARY KEY (isbn, author_id),
    FOREIGN KEY (isbn) REFERENCES Books (isbn),
    FOREIGN KEY (author_id) REFERENCES Authors (author_id)
);
CREATE TYPE order_status AS ENUM ('Pending', 'Confirmed');
CREATE TABLE IF NOT EXISTS PublisherOrders
(
    order_id   INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    isbn       VARCHAR(20) NOT NULL,
    quantity   INT         NOT NULL,
    order_date TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    status     order_status DEFAULT 'Pending',
    FOREIGN KEY (isbn) REFERENCES Books (isbn)
);
CREATE TABLE IF NOT EXISTS CustomerOrders
(
    order_id     INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id      INT NOT NULL,
    order_date   TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    total_amount DECIMAL(10, 2),
    status       order_status DEFAULT 'Pending',
    FOREIGN KEY (user_id) REFERENCES Users (user_id)
);
CREATE TABLE IF NOT EXISTS CustomerOrderItems
(
    order_id INT            NOT NULL,
    isbn     VARCHAR(20)    NOT NULL,
    quantity INT            NOT NULL,
    price    DECIMAL(10, 2) NOT NULL,
    PRIMARY KEY (order_id, isbn),
    FOREIGN KEY (order_id) REFERENCES CustomerOrders (order_id),
    FOREIGN KEY (isbn) REFERENCES Books (isbn)
);
CREATE TABLE IF NOT EXISTS ShoppingCart
(
    cart_id    INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id    INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES Users (user_id)
);
CREATE TABLE IF NOT EXISTS ShoppingCartItems
(
    cart_id  INT         NOT NULL,
    isbn     VARCHAR(20) NOT NULL,
    quantity INT         NOT NULL,
    PRIMARY KEY (cart_id, isbn),
    FOREIGN KEY (cart_id) REFERENCES ShoppingCart (cart_id),
    FOREIGN KEY (isbn) REFERENCES Books (isbn)
);
CREATE TABLE IF NOT EXISTS BillingInfo
(
    user_id         INT PRIMARY KEY,
    expiration_date DATE        NOT NULL,
    billing_address VARCHAR     NOT NULL,
    card_number     VARCHAR(20) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES Users (user_id)
    );

