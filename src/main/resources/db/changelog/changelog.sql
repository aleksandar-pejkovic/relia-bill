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
        ON DELETE CASCADE
        ON UPDATE CASCADE,
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
    plu INT NOT NULL,
    name VARCHAR(60) NOT NULL,
    unit VARCHAR(5),
    description VARCHAR(300),
    tax_rate INT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    in_stock DECIMAL(10, 2) DEFAULT 0.0,
    units_sold DECIMAL(10, 2) DEFAULT 0.0,
    revenue DECIMAL(10, 2) DEFAULT 0.0,
    username VARCHAR(60) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT unique_plu_per_user UNIQUE (plu, username),
    INDEX idx_plu (plu),
    INDEX idx_name (name)
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
    vat_status BOOLEAN,
    user_id INT(11),
    PRIMARY KEY (id),
    CONSTRAINT
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT unique_tax_number_per_user UNIQUE (tax_number, user_id),
    CONSTRAINT unique_registration_number_per_user UNIQUE (registration_number, user_id),
    CONSTRAINT unique_bank_account_per_user UNIQUE (bank_account, user_id),
    INDEX idx_name (name),
    INDEX idx_tax_number (tax_number),
    INDEX idx_registration_number (registration_number),
    INDEX idx_bank_account (bank_account),
    INDEX idx_user_id (user_id)
);

CREATE TABLE invoices (
    id INT(11) NOT NULL AUTO_INCREMENT,
    document_type VARCHAR(30) NOT NULL,
    invoice_number VARCHAR(60) NOT NULL,
    creation_date DATE NOT NULL,
    due_date DATE NOT NULL,
    invoice_status VARCHAR(15) NOT NULL,
    total DECIMAL(10,2),
    company_id INT(11) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT
        FOREIGN KEY (company_id)
        REFERENCES companies(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
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
        ON UPDATE CASCADE
);

CREATE TABLE password_reset_token (
    id INT(11) PRIMARY KEY AUTO_INCREMENT,
    token VARCHAR(255) NOT NULL,
    user_id INT(11) NOT NULL,
    expiry_date DATETIME NOT NULL,
    CONSTRAINT
        FOREIGN KEY (user_id)
        REFERENCES users (id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

--changeset apejkovic:2
INSERT INTO companies (id, name, director, registration_number, tax_number, bank_account, street, zip, city, phone,
email, website, vat_status)
VALUES
(
    1,
    'Alpey',
    'Aleksandar Pejković',
    '12345678',
    '987654321',
    '123-1004567-890',
    'Kralja Aleksandra 17',
    '37266',
    'Obrež',
    '0659170989',
    'info.reliabill@gmail.com',
    'reliabill.app',
    true
),
(
    2,
    'Alpey',
    'Aleksandar Pejković',
    '12345678',
    '987654321',
    '123-1004567-890',
    'Kralja Aleksandra 17',
    '11000',
    'Beograd',
    '0659170989',
    'info.reliabill@gmail.com',
    'reliabill.app',
    true
);

INSERT INTO users VALUES
(
    1,
    'pejko89',
    '$2a$10$c4k24Pk4lNy/v9wEZRsuT.LrTsYRLK7Jj7.mLahhCZwCgoWwAY7IW',
    'pejko89.ap@gmail.com',
    'Aleksandar Pejković',
    '2023-04-09',
    1
),
(
    2,
    'demo',
    '$2a$10$c4k24Pk4lNy/v9wEZRsuT.LrTsYRLK7Jj7.mLahhCZwCgoWwAY7IW',
    'aleksandar.pejkovic89@gmail.com',
    'Demo Tester',
    '2023-07-07',
    2
);

INSERT INTO users_roles(user_id, role_id) VALUES
    (1, 1),
    (2, 2);

INSERT INTO companies (id, name, director, registration_number, tax_number, bank_account, street, zip, city, phone,
email, website, vat_status, user_id)
VALUES (3, 'ABC Company', 'John Doe', '12345678', '987654321', '123-45672-90', 'Main Street', '12345', 'New York',
'123-456-7890', 'info@abccompany.com', 'www.abccompany.com', true, 2);

INSERT INTO companies (id, name, director, registration_number, tax_number, bank_account, street, zip, city, phone, email, website, vat_status, user_id)
VALUES (4, 'XYZ Corporation', 'Jane Smith', '87624321', '123451789', '198-26543-21', 'First Avenue', '54321', 'Los
Angeles',
 '987-654-3210', 'info@xyzcorp.com', 'www.xyzcorp.com', false, 2);

INSERT INTO companies (id, name, director, registration_number, tax_number, bank_account, street, zip, city, phone, email, website, vat_status, user_id)
VALUES (5, '123 Industries', 'Mike Johnson', '45628901', '947154321', '123-22678-90', 'Oak Street', '67890', 'Chicago',
'789-012-3456', 'info@123industries.com', 'www.123industries.com', true, 2);

INSERT INTO companies (id, name, director, registration_number, tax_number, bank_account, street, zip, city, phone, email, website, vat_status, user_id)
VALUES (6, 'ABC Company', 'Sarah Wilson', '78201234', '123456789', '109-8226543-21', 'Maple Avenue', '23456',
'San Francisco', '012-345-6789', 'info@abccompany.com', 'www.abccompany.com', false, 2);

INSERT INTO companies (id, name, director, registration_number, tax_number, bank_account, street, zip, city, phone, email, website, vat_status, user_id)
VALUES (7, 'XTR Corporation', 'David Lee', '56729012', '984624321', '123-421278-90', 'Cedar Street', '34567', 'Seattle',
'234-567-8901', 'info@xyzcorp.com', 'www.xyzcorp.com', true, 2);

INSERT INTO invoices (document_type, invoice_number, creation_date, due_date, invoice_status, total, company_id)
VALUES ('INVOICE', '2023-001', '2023-01-15', '2023-02-15', 'PENDING', 0.00, 3);

INSERT INTO invoices (document_type, invoice_number, creation_date, due_date, invoice_status, total, company_id)
VALUES ('INVOICE', '2023-002', '2023-02-05', '2023-03-05', 'PENDING', 0.00, 4);

INSERT INTO invoices (document_type, invoice_number, creation_date, due_date, invoice_status, total, company_id)
VALUES ('INVOICE', '2023-003', '2023-03-12', '2023-04-12', 'PENDING', 0.00, 5);

INSERT INTO invoices (document_type, invoice_number, creation_date, due_date, invoice_status, total, company_id)
VALUES ('INVOICE', '2023-004', '2023-04-08', '2023-05-08', 'PENDING', 0.00, 6);

INSERT INTO invoices (document_type, invoice_number, creation_date, due_date, invoice_status, total, company_id)
VALUES ('INVOICE', '2023-005', '2023-05-18', '2023-06-18', 'PENDING', 0.00, 7);

INSERT INTO invoices (document_type, invoice_number, creation_date, due_date, invoice_status, total, company_id)
VALUES ('INVOICE', '2023-006', '2023-01-10', '2023-02-10', 'PENDING', 0.00, 3);

INSERT INTO invoices (document_type, invoice_number, creation_date, due_date, invoice_status, total, company_id)
VALUES ('INVOICE', '2023-007', '2023-02-20', '2023-03-20', 'PENDING', 0.00, 4);

INSERT INTO invoices (document_type, invoice_number, creation_date, due_date, invoice_status, total, company_id)
VALUES ('INVOICE', '2023-008', '2023-03-25', '2023-04-25', 'PENDING', 0.00, 5);

INSERT INTO invoices (document_type, invoice_number, creation_date, due_date, invoice_status, total, company_id)
VALUES ('INVOICE', '2023-009', '2023-04-16', '2023-05-16', 'PENDING', 0.00, 6);

INSERT INTO invoices (document_type, invoice_number, creation_date, due_date, invoice_status, total, company_id)
VALUES ('INVOICE', '2023-010', '2023-05-28', '2023-06-28', 'PENDING', 0.00, 7);
