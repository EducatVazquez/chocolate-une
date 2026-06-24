-- =====================================================================
-- Datos iniciales – Chocolatería del Paraíso
-- =====================================================================

-- Categorías
INSERT INTO categories (name, description, active) VALUES
('Chocolates Amargos',  'Chocolates con 60% o más de cacao puro',         TRUE),
('Chocolates con Leche','Chocolates cremosos con leche entera',            TRUE),
('Chocolates Blancos',  'Elaborados con manteca de cacao sin sólidos',    TRUE),
('Bombones y Trufas',   'Bombones artesanales rellenos y trufas gourmet', TRUE),
('Sin Azúcar',          'Aptos para diabéticos y dietas bajas en azúcar', TRUE)
ON CONFLICT DO NOTHING;

-- Productos
INSERT INTO products (name, description, price, stock, image_url, active, category_id) VALUES
('Trufa de Chocolate Amargo 70%',     'Trufa artesanal de cacao venezolano 70%, con cobertura de polvo de cacao',          350.00, 50, NULL, TRUE, 1),
('Barra Amarga 85% Cacao',            'Tableta de origen único con 85% de cacao, notas frutales y acidez suave',           420.00, 35, NULL, TRUE, 1),
('Bombón de Avellana',                'Bombón relleno de praliné de avellana, cubierto en chocolate negro',                290.00, 80, NULL, TRUE, 4),
('Trufa de Champagne',                'Trufa de ganache infusionada con champagne Brut, cubierta en cacao',                380.00, 40, NULL, TRUE, 4),
('Barra de Chocolate con Leche',      'Clásica tableta de chocolate con leche entera y azúcar de caña',                   220.00, 120, NULL, TRUE, 2),
('Bombón Relleno de Dulce de Leche',  'Cáscara de chocolate con leche, relleno de dulce de leche artesanal',              260.00, 90, NULL, TRUE, 2),
('Barra de Chocolate Blanco con Vainilla', 'Chocolate blanco con extracto natural de vainilla Madagascar',                280.00, 60, NULL, TRUE, 3),
('Trufa de Limón y Jengibre',         'Ganache de limón y jengibre cubierta con chocolate blanco',                        320.00, 45, NULL, TRUE, 3),
('Barra Sin Azúcar 70%',              'Tableta amarga endulzada con stevia, sin azúcares añadidos',                       390.00, 30, NULL, TRUE, 5),
('Bombón de Frambuesa Sin Azúcar',    'Bombón relleno de coulis de frambuesa, apto para diabéticos',                      310.00, 25, NULL, TRUE, 5)
ON CONFLICT DO NOTHING;

-- Clientes
INSERT INTO customers (first_name, last_name, email, phone, address, active) VALUES
('Valentina', 'Rodríguez', 'valentina.rodriguez@email.com', '11-4523-7890', 'Av. Santa Fe 1234, CABA',         TRUE),
('Matías',    'González',  'matias.gonzalez@email.com',     '11-3891-2234', 'Corrientes 567, Rosario',          TRUE),
('Luciana',   'Martínez',  'luciana.martinez@email.com',    '11-5678-4521', 'Belgrano 890, Córdoba',            TRUE),
('Sebastián', 'López',     'sebastian.lopez@email.com',     '11-7712-3344', 'San Martín 321, Mendoza',          TRUE),
('Camila',    'Fernández', 'camila.fernandez@email.com',    '11-9900-1122', 'Libertador 4567, Mar del Plata',   TRUE)
ON CONFLICT DO NOTHING;
