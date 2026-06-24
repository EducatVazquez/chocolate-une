-- =====================================================================
-- Chocolatería del Paraíso – Script completo de base de datos
-- Motor: PostgreSQL 16
-- Base de datos: chocolateriadb
-- Usuario: chocolateria
-- =====================================================================

-- Crear el rol y la base de datos (ejecutar como superusuario si es necesario)
-- CREATE ROLE chocolateria WITH LOGIN PASSWORD 'chocolateria123';
-- CREATE DATABASE chocolateriadb OWNER chocolateria;
-- \c chocolateriadb

-- =====================================================================
-- Limpieza (orden inverso de dependencias)
-- =====================================================================
DROP TABLE IF EXISTS sale_details CASCADE;
DROP TABLE IF EXISTS sales       CASCADE;
DROP TABLE IF EXISTS products    CASCADE;
DROP TABLE IF EXISTS customers   CASCADE;
DROP TABLE IF EXISTS categories  CASCADE;

DROP TYPE IF EXISTS sale_status;

-- =====================================================================
-- Tipos personalizados
-- =====================================================================
CREATE TYPE sale_status AS ENUM ('PENDING', 'CONFIRMED', 'CANCELLED');

-- =====================================================================
-- Tablas
-- =====================================================================

CREATE TABLE categories (
    id          BIGSERIAL       PRIMARY KEY,
    name        VARCHAR(100)    NOT NULL UNIQUE,
    description VARCHAR(300),
    active      BOOLEAN         NOT NULL DEFAULT TRUE
);

CREATE TABLE customers (
    id         BIGSERIAL    PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name  VARCHAR(100) NOT NULL,
    email      VARCHAR(150) NOT NULL UNIQUE,
    phone      VARCHAR(20),
    address    VARCHAR(255),
    active     BOOLEAN      NOT NULL DEFAULT TRUE
);

CREATE TABLE products (
    id          BIGSERIAL        PRIMARY KEY,
    name        VARCHAR(200)     NOT NULL,
    description TEXT,
    price       NUMERIC(10, 2)   NOT NULL CHECK (price > 0),
    stock       INTEGER          NOT NULL CHECK (stock >= 0),
    image_url   VARCHAR(500),
    active      BOOLEAN          NOT NULL DEFAULT TRUE,
    category_id BIGINT           NOT NULL REFERENCES categories(id)
);

CREATE TABLE sales (
    id          BIGSERIAL        PRIMARY KEY,
    customer_id BIGINT           NOT NULL REFERENCES customers(id),
    sale_date   TIMESTAMP        NOT NULL DEFAULT NOW(),
    total       NUMERIC(12, 2)   NOT NULL DEFAULT 0.00,
    status      sale_status      NOT NULL DEFAULT 'CONFIRMED',
    notes       TEXT
);

CREATE TABLE sale_details (
    id          BIGSERIAL       PRIMARY KEY,
    sale_id     BIGINT          NOT NULL REFERENCES sales(id) ON DELETE CASCADE,
    product_id  BIGINT          NOT NULL REFERENCES products(id),
    quantity    INTEGER         NOT NULL CHECK (quantity >= 1),
    unit_price  NUMERIC(10, 2)  NOT NULL,
    subtotal    NUMERIC(12, 2)  NOT NULL
);

-- =====================================================================
-- Índices
-- =====================================================================
CREATE INDEX idx_products_category  ON products(category_id);
CREATE INDEX idx_sales_customer     ON sales(customer_id);
CREATE INDEX idx_sales_date         ON sales(sale_date);
CREATE INDEX idx_sale_details_sale  ON sale_details(sale_id);
CREATE INDEX idx_sale_details_prod  ON sale_details(product_id);

-- =====================================================================
-- Datos iniciales
-- =====================================================================

-- Categorías
INSERT INTO categories (name, description, active) VALUES
    ('Chocolates Amargos',       'Chocolates con 60% o más de cacao puro',         TRUE),
    ('Chocolates con Leche',     'Chocolates cremosos con leche entera',            TRUE),
    ('Chocolates Blancos',       'Elaborados con manteca de cacao sin sólidos',     TRUE),
    ('Bombones y Trufas',        'Bombones artesanales rellenos y trufas gourmet',  TRUE),
    ('Sin Azúcar',               'Aptos para diabéticos y dietas bajas en azúcar',  TRUE)
ON CONFLICT DO NOTHING;

-- Productos
INSERT INTO products (name, description, price, stock, image_url, active, category_id) VALUES
    ('Trufa de Chocolate Amargo 70%',          'Trufa artesanal de cacao venezolano 70%, con cobertura de polvo de cacao',       350.00, 50,  NULL, TRUE, 1),
    ('Barra Amarga 85% Cacao',                 'Tableta de origen único con 85% de cacao, notas frutales y acidez suave',        420.00, 35,  NULL, TRUE, 1),
    ('Bombón de Avellana',                     'Bombón relleno de praliné de avellana, cubierto en chocolate negro',              290.00, 80,  NULL, TRUE, 4),
    ('Trufa de Champagne',                     'Trufa de ganache infusionada con champagne Brut, cubierta en cacao',             380.00, 40,  NULL, TRUE, 4),
    ('Barra de Chocolate con Leche',           'Clásica tableta de chocolate con leche entera y azúcar de caña',                 220.00, 120, NULL, TRUE, 2),
    ('Bombón Relleno de Dulce de Leche',       'Cáscara de chocolate con leche, relleno de dulce de leche artesanal',            260.00, 90,  NULL, TRUE, 2),
    ('Barra de Chocolate Blanco con Vainilla', 'Chocolate blanco con extracto natural de vainilla Madagascar',                   280.00, 60,  NULL, TRUE, 3),
    ('Trufa de Limón y Jengibre',              'Ganache de limón y jengibre cubierta con chocolate blanco',                      320.00, 45,  NULL, TRUE, 3),
    ('Barra Sin Azúcar 70%',                   'Tableta amarga endulzada con stevia, sin azúcares añadidos',                     390.00, 30,  NULL, TRUE, 5),
    ('Bombón de Frambuesa Sin Azúcar',         'Bombón relleno de coulis de frambuesa, apto para diabéticos',                    310.00, 25,  NULL, TRUE, 5)
ON CONFLICT DO NOTHING;

-- Clientes
INSERT INTO customers (first_name, last_name, email, phone, address, active) VALUES
    ('Valentina', 'Rodríguez', 'valentina.rodriguez@email.com', '11-4523-7890', 'Av. Santa Fe 1234, CABA',        TRUE),
    ('Matías',    'González',  'matias.gonzalez@email.com',     '11-3891-2234', 'Corrientes 567, Rosario',         TRUE),
    ('Luciana',   'Martínez',  'luciana.martinez@email.com',    '11-5678-4521', 'Belgrano 890, Córdoba',           TRUE),
    ('Sebastián', 'López',     'sebastian.lopez@email.com',     '11-7712-3344', 'San Martín 321, Mendoza',         TRUE),
    ('Camila',    'Fernández', 'camila.fernandez@email.com',    '11-9900-1122', 'Libertador 4567, Mar del Plata',  TRUE)
ON CONFLICT DO NOTHING;

-- Ventas de ejemplo
INSERT INTO sales (customer_id, sale_date, total, status, notes) VALUES
    (1, '2026-06-10 10:30:00', 1050.00, 'CONFIRMED', NULL),
    (2, '2026-06-15 14:00:00',  840.00, 'CONFIRMED', 'Entrega a domicilio'),
    (3, '2026-06-20 09:15:00',  700.00, 'PENDING',   NULL);

-- Detalle de ventas de ejemplo
INSERT INTO sale_details (sale_id, product_id, quantity, unit_price, subtotal) VALUES
    (1, 1, 2, 350.00,  700.00),
    (1, 3, 1, 290.00,  290.00),
    (2, 5, 2, 220.00,  440.00),
    (2, 6, 1, 260.00,  260.00),
    (2, 8, 1, 320.00,  320.00),
    (3, 9, 1, 390.00,  390.00),
    (3, 2, 1, 420.00,  420.00)
ON CONFLICT DO NOTHING;
