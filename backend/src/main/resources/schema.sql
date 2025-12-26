SET search_path TO public;;

DO $$ 
BEGIN
    CREATE TYPE role_enum AS ENUM('Admin', 'Customer');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;;

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
);;

CREATE TABLE IF NOT EXISTS Publishers
(
    publisher_id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name         VARCHAR(100) NOT NULL,
    address      VARCHAR(255),
    phone        VARCHAR(20)
);;
DO $$ 
BEGIN
    CREATE TYPE category_type AS ENUM('Science', 'Art', 'Religion', 'History', 'Geography');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

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
);;

CREATE TABLE IF NOT EXISTS Authors
(
    author_id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name      VARCHAR(100) NOT NULL
);;

CREATE TABLE IF NOT EXISTS BookAuthors
(
    isbn      VARCHAR(20),
    author_id INT,
    PRIMARY KEY (isbn, author_id),
    FOREIGN KEY (isbn) REFERENCES Books (isbn),
    FOREIGN KEY (author_id) REFERENCES Authors (author_id)
);;

DO $$ 
BEGIN
    CREATE TYPE order_status AS ENUM ('Pending', 'Confirmed');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;;
CREATE TABLE IF NOT EXISTS PublisherOrders
(
    order_id   INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    isbn       VARCHAR(20) NOT NULL,
    quantity   INT         NOT NULL,
    order_date TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    status     order_status DEFAULT 'Pending',
    FOREIGN KEY (isbn) REFERENCES Books (isbn)
);;

CREATE TABLE IF NOT EXISTS CustomerOrders
(
    order_id     INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id      INT NOT NULL,
    order_date   TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    total_amount DECIMAL(10, 2),
    status       order_status DEFAULT 'Pending',
    FOREIGN KEY (user_id) REFERENCES Users (user_id)
);;

CREATE TABLE IF NOT EXISTS CustomerOrderItems
(
    order_id INT            NOT NULL,
    isbn     VARCHAR(20)    NOT NULL,
    quantity INT            NOT NULL,
    price    DECIMAL(10, 2) NOT NULL,
    PRIMARY KEY (order_id, isbn),
    FOREIGN KEY (order_id) REFERENCES CustomerOrders (order_id),
    FOREIGN KEY (isbn) REFERENCES Books (isbn)
);;

CREATE TABLE IF NOT EXISTS ShoppingCart
(
    cart_id    INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id    INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES Users (user_id)
);;

CREATE TABLE IF NOT EXISTS ShoppingCartItems
(
    cart_id  INT         NOT NULL,
    isbn     VARCHAR(20) NOT NULL,
    quantity INT         NOT NULL,
    PRIMARY KEY (cart_id, isbn),
    FOREIGN KEY (cart_id) REFERENCES ShoppingCart (cart_id),
    FOREIGN KEY (isbn) REFERENCES Books (isbn)
);;

CREATE TABLE IF NOT EXISTS BillingInfo
(
    user_id         INT PRIMARY KEY,
    expiration_date DATE        NOT NULL,
    billing_address VARCHAR     NOT NULL,
    card_number     VARCHAR(20) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES Users (user_id)
    );;

-- Refresh tokens table for JWT authentication
CREATE TABLE IF NOT EXISTS RefreshTokens
(
    token_id    INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id     INT       NOT NULL,
    token       VARCHAR(500) NOT NULL UNIQUE,
    expires_at  TIMESTAMP NOT NULL,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES Users (user_id) ON DELETE CASCADE
);;

CREATE INDEX IF NOT EXISTS idx_refresh_tokens_user_id ON RefreshTokens(user_id);;
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_token ON RefreshTokens(token);;
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_expires_at ON RefreshTokens(expires_at);;

CREATE OR REPLACE FUNCTION create_publisher_order_on_low_stock()
RETURNS TRIGGER AS $$
BEGIN
    IF OLD.number_of_books >= OLD.threshold AND NEW.number_of_books < NEW.threshold THEN
        INSERT INTO PublisherOrders (isbn, quantity, status)
        VALUES (NEW.isbn, 50, 'Pending'::order_status);
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;;

CREATE TRIGGER trigger_create_publisher_order
AFTER UPDATE ON Books
FOR EACH ROW
WHEN (OLD.number_of_books IS DISTINCT FROM NEW.number_of_books)
EXECUTE FUNCTION create_publisher_order_on_low_stock();;

CREATE OR REPLACE FUNCTION add_stock_on_order_confirmation()
RETURNS TRIGGER AS $$
BEGIN
    IF OLD.status = 'Pending'::order_status AND NEW.status = 'Confirmed'::order_status THEN
        UPDATE Books
        SET number_of_books = number_of_books + NEW.quantity
        WHERE isbn = NEW.isbn;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;;

CREATE TRIGGER trigger_add_stock_on_confirmation
AFTER UPDATE ON PublisherOrders
FOR EACH ROW
WHEN (OLD.status IS DISTINCT FROM NEW.status)
EXECUTE FUNCTION add_stock_on_order_confirmation();;

-- CREATE EXTENSION IF NOT EXISTS pg_trgm;
--
-- CREATE INDEX IF NOT EXISTS idx_books_title_trgm ON Books USING GIN (title pg_trgm_ops);
-- CREATE INDEX IF NOT EXISTS idx_books_category ON Books(category);
-- CREATE INDEX IF NOT EXISTS idx_authors_name_trgm ON Authors USING GIN (name pg_trgm_ops);
-- CREATE INDEX IF NOT EXISTS idx_publishers_name_trgm ON Publishers USING GIN (name pg_trgm_ops);

-- DROP TRIGGER IF EXISTS before_update_books ON Books;

-- CREATE TRIGGER before_update_books
-- BEFORE UPDATE ON Books
-- FOR EACH ROW
-- BEGIN
--     IF NEW.number_of_books < 0 THEN
--         RAISE EXCEPTION 'number of stocked books cannot be negative'
-- END$$

