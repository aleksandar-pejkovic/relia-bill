--liquibase formatted sql

--changeset apejkovic:1
CREATE TABLE users (
    id INT(11) NOT NULL AUTO_INCREMENT,
    username VARCHAR(60) NOT NULL UNIQUE,
    password VARCHAR(300) NOT NULL,
    email VARCHAR(60) NOT NULL UNIQUE,
    name VARCHAR(60) NOT NULL,
    creation_date DATE NOT NULL,
    company_id INT(11),
    PRIMARY KEY (id)
);

CREATE TABLE roles (
    id INT(11) NOT NULL AUTO_INCREMENT,
    name VARCHAR(36) NOT NULL UNIQUE,
    PRIMARY KEY (id)
);

INSERT INTO roles VALUES
	(1, 'ADMIN'),
	(2, 'USER');

CREATE TABLE users_roles (
  id INT(11) NOT NULL AUTO_INCREMENT,
  user_id INT(11) NOT NULL,
  role_id INT(11) NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT
    FOREIGN KEY (user_id)
    REFERENCES users(id)
    ON DELETE CASCADE,
  CONSTRAINT
    FOREIGN KEY (role_id)
    REFERENCES roles(id)
  );

CREATE TABLE permissions (
    id INT(11) NOT NULL AUTO_INCREMENT,
    name VARCHAR(36) NOT NULL UNIQUE,
    PRIMARY KEY (id)
);

INSERT INTO permissions VALUES
	(1, 'READ'),
	(2, 'WRITE'),
	(3, 'UPDATE'),
	(4, 'DELETE'),
	(5, 'GRANT_ADMIN'),
	(6, 'READ_EVENTS');

CREATE TABLE roles_permissions (
  id INT(11) NOT NULL AUTO_INCREMENT,
  role_id INT(11) NOT NULL,
  permission_id INT(11) NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT
    FOREIGN KEY (role_id)
    REFERENCES roles(id),
  CONSTRAINT
    FOREIGN KEY (permission_id)
    REFERENCES permissions(id)
  );

INSERT INTO roles_permissions (role_id, permission_id) VALUES
    (1, 2),
    (1, 3),
    (1, 4),
    (1, 5),
    (1, 6),
    (2, 1);

CREATE TABLE products (
  id INT(11) NOT NULL AUTO_INCREMENT,
  plu INT,
  name VARCHAR(60),
  unit VARCHAR(5),
  description VARCHAR(300),
  tax_rate INT,
  price DECIMAL(10,2),
  username VARCHAR(60),
  PRIMARY KEY (id)
);

CREATE TABLE companies (
    id INT(11) NOT NULL AUTO_INCREMENT,
    name VARCHAR(80) NOT NULL,
    director VARCHAR(60) NOT NULL,
    registration_number VARCHAR(8) NOT NULL,
    tax_number VARCHAR(9) NOT NULL,
    bank_account VARCHAR(20) NOT NULL,
    street VARCHAR(60),
    zip VARCHAR(5),
    city VARCHAR(60) NOT NULL,
    phone VARCHAR(15) NOT NULL,
    email VARCHAR(60) NOT NULL,
    website VARCHAR(60),
    user_id INT(11),
    PRIMARY KEY (id),
    CONSTRAINT
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);

CREATE TABLE invoices (
    id INT(11) NOT NULL AUTO_INCREMENT,
    document_type VARCHAR(30) NOT NULL,
    invoice_number VARCHAR(60) NOT NULL,
    creation_date DATE NOT NULL,
    due_date DATE NOT NULL,
    invoice_status VARCHAR(15) NOT NULL,
    total DECIMAL(10,2),
    tax DECIMAL(10,2),
    subtotal DECIMAL(10,2),
    total_for20 DECIMAL(10,2),
    tax_for20 DECIMAL(10,2),
    subtotal_for20 DECIMAL(10,2),
    total_for10 DECIMAL(10,2),
    tax_for10 DECIMAL(10,2),
    subtotal_for10 DECIMAL(10,2),
    total_for0 DECIMAL(10,2),
    company_id INT(11) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT
        FOREIGN KEY (company_id)
        REFERENCES companies(id)
        ON DELETE CASCADE
);

CREATE TABLE items (
    id INT(11) NOT NULL AUTO_INCREMENT,
    product_name VARCHAR(60),
    quantity DECIMAL(10,3),
    unit VARCHAR(5),
    price DECIMAL(10,2),
    tax_rate TINYINT,
    pre_tax DECIMAL(10,2),
    total DECIMAL(10,2),
    tax DECIMAL(10,2),
    subtotal DECIMAL(10,2),
    invoice_id INT(11) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT
        FOREIGN KEY (invoice_id)
        REFERENCES invoices (id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE payments (
  id INT(11) NOT NULL AUTO_INCREMENT,
  payment_date DATETIME NOT NULL,
  amount DOUBLE PRECISION NOT NULL,
  invoice_id INT(11) NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT
    FOREIGN KEY (invoice_id)
    REFERENCES invoices(id)
    ON DELETE CASCADE
);

--changeset apejkovic:2
INSERT INTO users (username, password, email, name, creation_date) VALUES
(
    'pejko89',
    '$2a$10$c4k24Pk4lNy/v9wEZRsuT.LrTsYRLK7Jj7.mLahhCZwCgoWwAY7IW',
    'pejko1989@live.com',
    'Aleksandar Pejkovic',
    '2023-04-09'
);

INSERT INTO users_roles(user_id, role_id) VALUES
    (1, 1);
