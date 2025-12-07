-- ============================================
-- SCRIPT DE INSERCIÓN DE DATOS - CINEPLUS DB
-- Fecha: 2025-12-07
-- Descripción: Inserts organizados por dependencias de FK
-- ============================================

-- IMPORTANTE: Ejecutar en este orden exacto para respetar las foreign keys

-- ============================================
-- PASO 1: TABLAS INDEPENDIENTES (Sin FK)
-- ============================================

-- NOTA: Los roles (ROLE_ADMIN, ROLE_MANAGER, ROLE_USER) se crean automáticamente 
-- por DataLoader.java al iniciar la aplicación. No es necesario insertarlos manualmente.

-- 1. MOVIES (21 películas con datos reales)
    INSERT INTO movies (title, synopsis, genre, classification, duration, card_image_url, banner_url, trailer_url, status) VALUES
    -- NOW_PLAYING (id 1-10) -> CARTELERA
    ('Concierto de Navidad de André Rieu 2025', 'Esta temporada navideña, la magia comienza en la gran pantalla. Únete a André Rieu en su concierto de Navidad 2025.', 'Concierto', 'R', '2hrs 35min', 'https://cdn.apis.cineplanet.com.pe/CDN/media/entity/get/FilmPosterGraphic/HO00002676?referenceScheme=HeadOffice&allowPlaceHolder=true', NULL, NULL, 'CARTELERA'),
    ('Beso de tres', 'Connor, un joven amable pero inseguro, lleva años enamorado de su amiga Olivia y queda atrapado en un trío inesperado.', 'Drama', '+14', '1hrs 52min', 'https://cdn.apis.cineplanet.com.pe/CDN/media/entity/get/FilmPosterGraphic/HO00002628?referenceScheme=HeadOffice&allowPlaceHolder=true', NULL, 'https://youtu.be/bBDpvM6sPEY', 'CARTELERA'),
    ('El lado oscuro de la justicia', 'Un joven pobre es acusado de tráfico de drogas. Un exfiscal investiga el caso y descubre un plan de abogados corruptos.', 'Acción', '+14', '2hrs 0min', 'https://cdn.apis.cineplanet.com.pe/CDN/media/entity/get/FilmPosterGraphic/HO00002697?referenceScheme=HeadOffice&allowPlaceHolder=true', 'https://cdnpe.cineplanet.com.pe/assets/1072558e-e442-4c8c-95c9-60bf1c13bfa8', 'https://www.youtube.com/watch?v=ycXCAIUZLHw', 'CARTELERA'),
    ('Fue Solo Un Accidente', 'Lo que comenzó como un pequeño accidente pone en movimiento una serie de consecuencias cada vez mayores.', 'Drama', '+14', '1hrs 45min', 'https://cdn.apis.cineplanet.com.pe/CDN/media/entity/get/FilmPosterGraphic/HO00002698?referenceScheme=HeadOffice&allowPlaceHolder=true', 'https://cdnpe.cineplanet.com.pe/assets/85825100-c329-49bf-bd93-0b9002406fda', 'https://www.youtube.com/watch?v=BXJbgWNcDxE', 'CARTELERA'),
("Five Nights at Freddy's 2", 'Ha pasado un año desde la pesadilla sobrenatural en Freddy Fazbear''s Pizza. La familia vuelve a enfrentar el terror.', 'Terror', '+14', '1hrs 45min', 'https://cdn.apis.cineplanet.com.pe/CDN/media/entity/get/FilmPosterGraphic/HO00002582?referenceScheme=HeadOffice&allowPlaceHolder=true', 'https://cdnpe.cineplanet.com.pe/assets/80f68fae-db70-45cb-87bd-5392a78dded2', 'https://www.youtube.com/watch?v=E8M-iJ0p-Xk', 'CARTELERA'),
    ('Kiki Entregas a Domicilio', 'Kiki es una joven bruja que al cumplir 13 años debe encontrar su lugar en el mundo con su servicio de entregas voladoras.', 'Anime', 'APT', '1hrs 52min', 'https://cdn.apis.cineplanet.com.pe/CDN/media/entity/get/FilmPosterGraphic/HO00002503?referenceScheme=HeadOffice&allowPlaceHolder=true', NULL, 'https://www.youtube.com/watch?v=lHhMseKJFWY', 'CARTELERA'),
    ('Sirenas', 'En La Casa de las Sirenas, el burdel más antiguo de Iquitos, el carnaval se torna en misterio al hallarse muerta a la menor.', 'Thriller', '+14', '1hrs 45min', 'https://cdn.apis.cineplanet.com.pe/CDN/media/entity/get/FilmPosterGraphic/HO00002689?referenceScheme=HeadOffice&allowPlaceHolder=true', 'https://cdnpe.cineplanet.com.pe/assets/e56b2a1f-03d9-4bcf-98bf-10d54ee88b17', 'https://youtu.be/etE2HYY7tRI', 'CARTELERA'),
    ('Una Navidad Inesperada', 'Una pareja lleva a su hija al hotel de su abuelo para revelarle su separación, pero la niña intentará reunir a la familia.', 'Comedia', '+14', '1hrs 32min', 'https://cdn.apis.cineplanet.com.pe/CDN/media/entity/get/FilmPosterGraphic/HO00002690?referenceScheme=HeadOffice&allowPlaceHolder=true', 'https://cdnpe.cineplanet.com.pe/assets/7c4fbc84-f4bd-4697-9d8b-7362703054bc', 'https://www.youtube.com/watch?v=mHDz_kgQOuI', 'CARTELERA'),
    ('MONSTA X: CONNECT X IN CINEMAS', 'MONSTA X incendió el KSPO DOME durante tres noches inolvidables. Una crónica de diez años juntos.', 'Concierto', '+14', '1hrs 58min', 'https://cdn.apis.cineplanet.com.pe/CDN/media/entity/get/FilmPosterGraphic/HO00002675?referenceScheme=HeadOffice&allowPlaceHolder=true', NULL, 'https://www.youtube.com/watch?v=P11rZPHKxb4', 'CARTELERA'),
    ('Zootopia 2', 'Los detectives Judy Hopps y Nick Wilde se enfrentan a su misión más salvaje: un misterioso reptil llegó a la ciudad.', 'Animación', 'APT', '1hrs 48min', 'https://cdn.apis.cineplanet.com.pe/CDN/media/entity/get/FilmPosterGraphic/HO00002477?referenceScheme=HeadOffice&allowPlaceHolder=true', 'https://i.pinimg.com/1200x/d4/9c/cd/d49ccd324765da7c7b4a39f0105f99a7.jpg', 'https://www.youtube.com/watch?v=4_KaABhCPPk', 'CARTELERA'),
-- PRE_SALE (id 11-15) -> PREVENTA
('Crítica Abierta: Bugonia', 'Función especial con conversatorio. Dos jóvenes secuestran a una CEO convencidos de que es una alienígena.', 'Drama', '+14', '2hrs 0min', 'https://cdn.apis.cineplanet.com.pe/CDN/media/entity/get/FilmPosterGraphic/HO00002719?referenceScheme=HeadOffice&allowPlaceHolder=true', NULL, 'https://www.youtube.com/watch?v=jVp8DR0ObDU', 'PREVENTA'),
('Amanecer Parte 1', 'Edward y Bella se casan. Durante la luna de miel, Bella queda embarazada y su salud se deteriora rápidamente.', 'Romance', '+14', '1hrs 57min', 'https://cdn.apis.cineplanet.com.pe/CDN/media/entity/get/FilmPosterGraphic/HO00002653?referenceScheme=HeadOffice&allowPlaceHolder=true', NULL, NULL, 'PREVENTA'),
('Amanecer Parte 2', 'Bella se adapta a su nueva naturaleza vampira. La familia Cullen debe protegerse de la amenaza de los Volturi.', 'Romance', '+14', '1hrs 58min', 'https://cdn.apis.cineplanet.com.pe/CDN/media/entity/get/FilmPosterGraphic/HO00002654?referenceScheme=HeadOffice&allowPlaceHolder=true', NULL, 'https://www.youtube.com/watch?v=zJL0l-7MuWw', 'PREVENTA'),
('El Grinch', 'En las afueras de Whoville, vive el Grinch y busca venganza para arruinar la Navidad de todos.', 'Familiar', 'APT', '1hrs 44min', 'https://cdn.apis.cineplanet.com.pe/CDN/media/entity/get/FilmPosterGraphic/HO00002701?referenceScheme=HeadOffice&allowPlaceHolder=true', NULL, NULL, 'PREVENTA'),
('Avatar Fuego y Cenizas', 'La familia de Jake y Neytiri se enfrenta a una tribu Na''vi hostil, los Ash, mientras los conflictos en Pandora se intensifican.', 'Acción', '+14', '3hrs 14min', 'https://cdn.apis.cineplanet.com.pe/CDN/media/entity/get/FilmPosterGraphic/HO00002638?referenceScheme=HeadOffice&allowPlaceHolder=true', NULL, 'https://www.youtube.com/watch?v=g71Ha1HCWt8', 'PREVENTA'),
-- COMING_SOON (id 16-21) -> PROXIMO
('100 Metros', 'Togashi, estrella del atletismo, conoce al estudiante Komiya. Pasarán los años y sus destinos se cruzarán como rivales.', 'Anime', '+14', '1hrs 46min', 'https://cdn.apis.cineplanet.com.pe/CDN/media/entity/get/FilmPosterGraphic/HO00002700?referenceScheme=HeadOffice&allowPlaceHolder=true', NULL, NULL, 'PROXIMO'),
('Bugonia', 'Dos jóvenes obsesionados con conspiraciones secuestran a una CEO convencidos de que es una alienígena.', 'Drama', '+14', '2hrs 0min', 'https://cdn.apis.cineplanet.com.pe/CDN/media/entity/get/FilmPosterGraphic/HO00002666?referenceScheme=HeadOffice&allowPlaceHolder=true', NULL, NULL, 'PROXIMO'),
('El Descubridor de Leyendas', 'El profesor Fang lidera una expedición al Templo del Glaciar en busca de artefactos que conectan sueños y realidad.', 'Acción', '+14', '2hrs 0min', 'https://cdn.apis.cineplanet.com.pe/CDN/media/entity/get/FilmPosterGraphic/HO00002718?referenceScheme=HeadOffice&allowPlaceHolder=true', NULL, 'https://www.youtube.com/watch?v=cEFCr0B6udc', 'PROXIMO'),
('El gran premio a toda velocidad', 'Edda, una joven ratoncita, tiene la oportunidad de competir en el Gran Premio disfrazada como su ídolo.', 'Animación', 'APT', '2hrs 0min', 'https://cdn.apis.cineplanet.com.pe/CDN/media/entity/get/FilmPosterGraphic/HO00002478?referenceScheme=HeadOffice&allowPlaceHolder=true', NULL, 'https://www.youtube.com/watch?v=cEFCr0B6udc', 'PROXIMO'),
('Fuimos Héroes', 'En 1975, la selección peruana conquistó su segunda Copa América. Esta es la historia de esos héroes.', 'Drama', '+14', '55min', 'https://cdn.apis.cineplanet.com.pe/CDN/media/entity/get/FilmPosterGraphic/HO00002685?referenceScheme=HeadOffice&allowPlaceHolder=true', NULL, NULL, 'PROXIMO'),
('Noche de Paz, Noche de Horror', 'Un niño presencia el asesinato de sus padres. Años después, se disfraza de Papá Noel y busca venganza.', 'Terror', '+14', '1hrs 36min', 'https://cdn.apis.cineplanet.com.pe/CDN/media/entity/get/FilmPosterGraphic/HO00002662?referenceScheme=HeadOffice&allowPlaceHolder=true', NULL, NULL, 'PROXIMO');

-- 2. CINEMAS (8 cines reales de Perú)
INSERT INTO cinemas (name, city, address, location, image) VALUES
('Cineplus Real Plaza Trujillo', 'Trujillo', 'Av. América Sur 1111, Trujillo', 'Real Plaza Trujillo', 'https://i.imgur.com/EyXfJf3.png'),
('Cineplus Angamos', 'Lima', 'Av. Angamos Oeste 1803, Miraflores', 'Angamos', 'https://i.imgur.com/YSIYTYI.png'),
('Cineplus Arequipa', 'Arequipa', 'Av. Ejército 793, Cayma', 'Arequipa', 'https://i.imgur.com/M6qIkFc.png'),
('Cineplus Asia', 'Lima', 'Km 97.5 Panamericana Sur, Asia', 'Asia', 'https://i.imgur.com/OhzUP03.png'),
('Cineplus Bellavista', 'Callao', 'Av. Colonial 4000, Bellavista', 'Bellavista', 'https://i.imgur.com/ewcJ4fD.png'),
('Cineplus Gamarra', 'Lima', 'Jr. Gamarra 1000, La Victoria', 'Gamarra', 'https://i.imgur.com/nynFqFZ.png'),
('Cineplus Jockey Plaza', 'Lima', 'Av. Javier Prado Este 4200, Santiago de Surco', 'Jockey Plaza', 'https://i.imgur.com/5pVThyZ.png'),
('Cineplus Lambra', 'Lima', 'Av. La Molina 1100, La Molina', 'Lambra', 'https://i.imgur.com/LD1nrhu.png');

-- 3. CONCESSION_PRODUCTS
INSERT INTO concession_products (name, description, price, category, image_url, available) VALUES
('Combo Caliente', 'Canchita grande + Gaseosa grande + Hot dog', 50.20, 'COMBOS', 'https://i.imgur.com/3QXTlTP.png', TRUE),
('Combo Sal', 'Canchita mediana + Gaseosa grande + Nachos', 48.30, 'COMBOS', 'https://i.imgur.com/mn8MkFr.png', TRUE),
('Combo Familiar', '4 Canchitas grandes + 3 Gaseosas grandes', 149.00, 'COMBOS', 'https://i.imgur.com/8mqvkF4.png', TRUE),
('Combo Dulce', 'Canchita grande dulce + Frugos + Nuggets x6', 47.40, 'COMBOS', 'https://i.imgur.com/E9ZykEX.png', TRUE),
('Combo Pareja', '2 Canchitas medianas + 2 Gaseosas grandes', 69.90, 'COMBOS', 'https://i.imgur.com/dkPseQ3.png', TRUE),
('Combo Burger Premium', 'Canchita gigante + 2 Gaseosas grandes + Hamburguesa', 35.90, 'COMBOS', 'https://i.imgur.com/4f5OE64.png', TRUE),
('Canchita Gigante Salada', 'Porción gigante de canchita con sal', 34.90, 'CANCHITA', 'https://i.imgur.com/6X0TjP9.png', TRUE),
('Canchita Grande Salada', 'Porción grande de canchita con sal', 29.90, 'CANCHITA', 'https://i.imgur.com/yoalqQt.png', TRUE),
('Canchita Grande Dulce', 'Porción grande de canchita dulce', 31.90, 'CANCHITA', 'https://i.imgur.com/4BCV5nd.png', TRUE),
('Canchita Mediana Salada', 'Porción mediana de canchita con sal', 26.90, 'CANCHITA', 'https://i.imgur.com/uJlVCc9.png', TRUE),
('Canchita Mediana Dulce', 'Porción mediana de canchita dulce', 27.90, 'CANCHITA', 'https://i.imgur.com/UCS4p8y.png', TRUE),
('Canchita Kids Salada', 'Porción Kids de canchita con sal', 11.90, 'CANCHITA', 'https://i.imgur.com/JBRSxds.png', TRUE),
('Canchita Kids Dulce', 'Canchita Kids dulce', 12.90, 'CANCHITA', 'https://i.imgur.com/Nu0Ow6U.png', TRUE),
('Canchita Mantequilla', 'Canchita grande con mantequilla', 31.90, 'CANCHITA', 'https://i.imgur.com/oSnb1b2.png', TRUE),
('Coca Cola Grande', 'Coca Cola 500ml', 11.90, 'BEBIDAS', 'https://i.imgur.com/H0od7eK.png', TRUE),
('Inca Kola Grande', 'Inca Kola 500ml', 11.90, 'BEBIDAS', 'https://i.imgur.com/nUkZc3u.png', TRUE),
('Sprite Grande', 'Sprite 500ml', 11.90, 'BEBIDAS', 'https://i.imgur.com/YrRj40A.png', TRUE),
('Jugo de Naranja', 'Jugo natural de naranja 400ml', 11.90, 'BEBIDAS', 'https://i.imgur.com/1gzH5cZ.png', TRUE),
('Frugos del valle', 'Frugos 300ml', 6.90, 'BEBIDAS', 'https://i.imgur.com/wPEQYeE.png', TRUE),
('Agua con Gas', 'Agua mineral con gas 500ml', 6.90, 'BEBIDAS', 'https://i.imgur.com/2S1tlC6.png', TRUE),
('Agua Sin Gas', 'Agua mineral 500ml', 5.90, 'BEBIDAS', 'https://i.imgur.com/kAjLTU3.png', TRUE),
('Hot Dog Frankfurter', 'Hot dog con salchicha alemana y salsas', 13.90, 'SNACKS', 'https://i.imgur.com/vgYqN6n.png', TRUE),
('Nachos con Queso', 'Nachos crujientes con salsa de queso', 14.90, 'SNACKS', 'https://i.imgur.com/fmBuiPG.png', TRUE),
('Papas Fritas', 'Papas fritas crujientes porción grande', 7.90, 'SNACKS', 'https://i.imgur.com/X5f8YC9.png', TRUE),
('Tequeños x4 un', '4 tequeños de queso fritos', 10.90, 'SNACKS', 'https://i.imgur.com/bDMNPBk.png', TRUE),
('Nuggets x6', '6 nuggets de pollo crujientes', 13.90, 'SNACKS', 'https://i.imgur.com/MtnELKD.png', TRUE),
('Salchipapas', 'Papas fritas con salchicha y salsas', 14.90, 'SNACKS', 'https://i.imgur.com/aF0NUuv.png', TRUE),
('Hamburguesa Clásica', 'Hamburguesa con carne, lechuga y tomate', 16.90, 'SNACKS', 'https://i.imgur.com/8U3R1Oa.png', TRUE),
('Pizza Personal', 'Pizza individual de pepperoni', 18.90, 'SNACKS', 'https://i.imgur.com/oMEqVGb.png', TRUE),
('Sandwich Club', 'Sandwich triple con pollo, tocino y verduras', 16.90, 'SNACKS', 'https://i.imgur.com/jwVxk1i.png', TRUE),
('Alitas BBQ x7', '7 alitas de pollo con salsa BBQ', 18.90, 'SNACKS', 'https://i.imgur.com/cXOORTE.png', TRUE),
('Quesadilla', 'Quesadilla de queso con guacamole', 15.90, 'SNACKS', 'https://i.imgur.com/oK4ZOoP.png', TRUE),
('Wrap de Pollo', 'Wrap con pollo, lechuga y salsa ranch', 19.90, 'SNACKS', 'https://i.imgur.com/VwpHY4O.png', TRUE);

-- 4. PROMOTIONS
INSERT INTO promotions (code, description, discount_type, value, start_date, end_date, max_uses, current_uses, min_amount, is_active) VALUES
('NAVIDAD25', '25% de descuento por Navidad', 'PERCENTAGE', 25.00, '2025-12-15 00:00:00', '2025-12-26 23:59:59', 500, 0, 20.00, TRUE),
('ESTUDIANTE15', '15% descuento estudiantil', 'PERCENTAGE', 15.00, '2025-12-01 00:00:00', '2026-03-31 23:59:59', 1500, 0, 10.00, TRUE);

-- 5. TICKET_TYPES
-- NOTA: Los ticket_types se crean automáticamente por DataLoader.java si la tabla está vacía.
-- Si ejecutas este INSERT manualmente, el DataLoader NO los creará.
-- Columnas: code, name, price, active (NO tiene 'description' ni 'is_active')
INSERT INTO ticket_types (code, name, price, active) VALUES
('PROMO_ONLINE', 'PROMO ONLINE', 14.96, TRUE),
('PERSONA_CON_DISCAPACIDAD', 'PERSONA CON DISCAPACIDAD', 17.70, TRUE),
('SILLA_DE_RUEDAS', 'SILLA DE RUEDAS', 17.70, TRUE),
('NINO', 'NIÑO', 21.60, TRUE),
('ADULTO', 'ADULTO', 23.60, TRUE),
('50_DCTO_BANCO_RIPLEY', '50% DCTO BANCO RIPLEY', 12.80, TRUE);

-- ============================================
-- PASO 2: TABLAS CON 1 NIVEL DE DEPENDENCIA
-- ============================================

-- 6. USERS
-- NOTA: Las contraseñas están encriptadas con BCrypt
-- Usuario: admin     | Contraseña: admin123
-- Usuario: diego123  | Contraseña: diego123
INSERT INTO users (username, national_id, first_name, last_name, email, birth_date, gender, password) VALUES
('admin', '00000000', 'Admin', 'Sistema', 'admin@cineplus.com', '1990-01-01', 'Otro', '$2a$10$N9qo8uLOickgx2ZMRZoMye1J3uv5Le1'),
('diego123', '12345678', 'Diego', 'Falla', 'diego@mail.com', '1995-03-15', 'Masculino', '$2a$10$N9qo8uLOickgx2ZMRZoMye2J3uv5Le2');

-- 7. USER_ROLES
-- NOTA: Los role_id se asignan automáticamente: 1=ROLE_ADMIN, 2=ROLE_MANAGER, 3=ROLE_USER
INSERT INTO user_roles (user_id, role_id) VALUES
(1, 1), -- admin tiene ROLE_ADMIN
(1, 2), -- admin tiene ROLE_MANAGER (múltiples roles)
(2, 3); -- diego123 tiene ROLE_USER

-- 8. THEATERS (96 salas total: 8 cines × 12 salas)
-- Cada cine: 3 XD (LARGE/LARGE/MEDIUM), 3 3D (MEDIUM/MEDIUM/SMALL), 6 2D (LARGE×2/MEDIUM×2/SMALL×2)
INSERT INTO theaters (cinema_id, name, seat_matrix_type, row_count, col_count, total_seats) VALUES
-- Cinema 1: Trujillo (theater_id 1-12)
(1, 'Sala XD 1', 'LARGE', 12, 15, 180), (1, 'Sala XD 2', 'LARGE', 12, 15, 180), (1, 'Sala XD 3', 'MEDIUM', 10, 12, 120),
(1, 'Sala 3D 1', 'MEDIUM', 10, 12, 120), (1, 'Sala 3D 2', 'MEDIUM', 10, 12, 120), (1, 'Sala 3D 3', 'SMALL', 8, 10, 80),
(1, 'Sala 2D 1', 'LARGE', 12, 14, 168), (1, 'Sala 2D 2', 'LARGE', 12, 14, 168), (1, 'Sala 2D 3', 'MEDIUM', 10, 12, 120),
(1, 'Sala 2D 4', 'MEDIUM', 10, 12, 120), (1, 'Sala 2D 5', 'SMALL', 8, 10, 80), (1, 'Sala 2D 6', 'SMALL', 8, 10, 80),
-- Cinema 2: Angamos (theater_id 13-24)
(2, 'Sala XD 1', 'LARGE', 12, 15, 180), (2, 'Sala XD 2', 'LARGE', 12, 15, 180), (2, 'Sala XD 3', 'MEDIUM', 10, 12, 120),
(2, 'Sala 3D 1', 'MEDIUM', 10, 12, 120), (2, 'Sala 3D 2', 'MEDIUM', 10, 12, 120), (2, 'Sala 3D 3', 'SMALL', 8, 10, 80),
(2, 'Sala 2D 1', 'LARGE', 12, 14, 168), (2, 'Sala 2D 2', 'LARGE', 12, 14, 168), (2, 'Sala 2D 3', 'MEDIUM', 10, 12, 120),
(2, 'Sala 2D 4', 'MEDIUM', 10, 12, 120), (2, 'Sala 2D 5', 'SMALL', 8, 10, 80), (2, 'Sala 2D 6', 'SMALL', 8, 10, 80),
-- Cinema 3: Arequipa (theater_id 25-36)
(3, 'Sala XD 1', 'LARGE', 12, 15, 180), (3, 'Sala XD 2', 'LARGE', 12, 15, 180), (3, 'Sala XD 3', 'MEDIUM', 10, 12, 120),
(3, 'Sala 3D 1', 'MEDIUM', 10, 12, 120), (3, 'Sala 3D 2', 'MEDIUM', 10, 12, 120), (3, 'Sala 3D 3', 'SMALL', 8, 10, 80),
(3, 'Sala 2D 1', 'LARGE', 12, 14, 168), (3, 'Sala 2D 2', 'LARGE', 12, 14, 168), (3, 'Sala 2D 3', 'MEDIUM', 10, 12, 120),
(3, 'Sala 2D 4', 'MEDIUM', 10, 12, 120), (3, 'Sala 2D 5', 'SMALL', 8, 10, 80), (3, 'Sala 2D 6', 'SMALL', 8, 10, 80),
-- Cinema 4: Asia (theater_id 37-48)
(4, 'Sala XD 1', 'LARGE', 12, 15, 180), (4, 'Sala XD 2', 'LARGE', 12, 15, 180), (4, 'Sala XD 3', 'MEDIUM', 10, 12, 120),
(4, 'Sala 3D 1', 'MEDIUM', 10, 12, 120), (4, 'Sala 3D 2', 'MEDIUM', 10, 12, 120), (4, 'Sala 3D 3', 'SMALL', 8, 10, 80),
(4, 'Sala 2D 1', 'LARGE', 12, 14, 168), (4, 'Sala 2D 2', 'LARGE', 12, 14, 168), (4, 'Sala 2D 3', 'MEDIUM', 10, 12, 120),
(4, 'Sala 2D 4', 'MEDIUM', 10, 12, 120), (4, 'Sala 2D 5', 'SMALL', 8, 10, 80), (4, 'Sala 2D 6', 'SMALL', 8, 10, 80),
-- Cinema 5: Bellavista (theater_id 49-60)
(5, 'Sala XD 1', 'LARGE', 12, 15, 180), (5, 'Sala XD 2', 'LARGE', 12, 15, 180), (5, 'Sala XD 3', 'MEDIUM', 10, 12, 120),
(5, 'Sala 3D 1', 'MEDIUM', 10, 12, 120), (5, 'Sala 3D 2', 'MEDIUM', 10, 12, 120), (5, 'Sala 3D 3', 'SMALL', 8, 10, 80),
(5, 'Sala 2D 1', 'LARGE', 12, 14, 168), (5, 'Sala 2D 2', 'LARGE', 12, 14, 168), (5, 'Sala 2D 3', 'MEDIUM', 10, 12, 120),
(5, 'Sala 2D 4', 'MEDIUM', 10, 12, 120), (5, 'Sala 2D 5', 'SMALL', 8, 10, 80), (5, 'Sala 2D 6', 'SMALL', 8, 10, 80),
-- Cinema 6: Gamarra (theater_id 61-72)
(6, 'Sala XD 1', 'LARGE', 12, 15, 180), (6, 'Sala XD 2', 'LARGE', 12, 15, 180), (6, 'Sala XD 3', 'MEDIUM', 10, 12, 120),
(6, 'Sala 3D 1', 'MEDIUM', 10, 12, 120), (6, 'Sala 3D 2', 'MEDIUM', 10, 12, 120), (6, 'Sala 3D 3', 'SMALL', 8, 10, 80),
(6, 'Sala 2D 1', 'LARGE', 12, 14, 168), (6, 'Sala 2D 2', 'LARGE', 12, 14, 168), (6, 'Sala 2D 3', 'MEDIUM', 10, 12, 120),
(6, 'Sala 2D 4', 'MEDIUM', 10, 12, 120), (6, 'Sala 2D 5', 'SMALL', 8, 10, 80), (6, 'Sala 2D 6', 'SMALL', 8, 10, 80),
-- Cinema 7: Jockey Plaza (theater_id 73-84)
(7, 'Sala XD 1', 'LARGE', 12, 15, 180), (7, 'Sala XD 2', 'LARGE', 12, 15, 180), (7, 'Sala XD 3', 'MEDIUM', 10, 12, 120),
(7, 'Sala 3D 1', 'MEDIUM', 10, 12, 120), (7, 'Sala 3D 2', 'MEDIUM', 10, 12, 120), (7, 'Sala 3D 3', 'SMALL', 8, 10, 80),
(7, 'Sala 2D 1', 'LARGE', 12, 14, 168), (7, 'Sala 2D 2', 'LARGE', 12, 14, 168), (7, 'Sala 2D 3', 'MEDIUM', 10, 12, 120),
(7, 'Sala 2D 4', 'MEDIUM', 10, 12, 120), (7, 'Sala 2D 5', 'SMALL', 8, 10, 80), (7, 'Sala 2D 6', 'SMALL', 8, 10, 80),
-- Cinema 8: Lambra (theater_id 85-96)
(8, 'Sala XD 1', 'LARGE', 12, 15, 180), (8, 'Sala XD 2', 'LARGE', 12, 15, 180), (8, 'Sala XD 3', 'MEDIUM', 10, 12, 120),
(8, 'Sala 3D 1', 'MEDIUM', 10, 12, 120), (8, 'Sala 3D 2', 'MEDIUM', 10, 12, 120), (8, 'Sala 3D 3', 'SMALL', 8, 10, 80),
(8, 'Sala 2D 1', 'LARGE', 12, 14, 168), (8, 'Sala 2D 2', 'LARGE', 12, 14, 168), (8, 'Sala 2D 3', 'MEDIUM', 10, 12, 120),
(8, 'Sala 2D 4', 'MEDIUM', 10, 12, 120), (8, 'Sala 2D 5', 'SMALL', 8, 10, 80), (8, 'Sala 2D 6', 'SMALL', 8, 10, 80);

-- 9. MOVIE_CAST
INSERT INTO movie_cast (movie_id, cast_member) VALUES
(1, 'André Rieu'), (1, 'Emma Kok'),
(2, 'Zoey Deutch'), (2, 'Jonah Hauer-King'),
(3, 'Donnie Yen'), (3, 'Francis Ng'),
(4, 'Ebrahim Azizi'), (4, 'Madjid Panahi'),
(5, 'Josh Hutcherson'), (5, 'Elizabeth Lail'), (5, 'Matthew Lillard'),
(6, 'Minami Takayama'), (6, 'Rei Sakuma'),
(7, 'Lucy Bacigalupo'), (7, 'Diva Rivera'),
(8, 'Tyler Hynes'), (8, 'Bethany Joy Lenz'),
(9, 'Shownu'), (9, 'Minhyuk'), (9, 'Kihyun'),
(10, 'Ginnifer Goodwin'), (10, 'Jason Bateman'), (10, 'Shakira'),
(11, 'Emma Stone'), (11, 'Jesse Plemons'),
(12, 'Kristen Stewart'), (12, 'Robert Pattinson'), (12, 'Taylor Lautner'),
(13, 'Kristen Stewart'), (13, 'Robert Pattinson'),
(14, 'Jim Carrey'), (14, 'Taylor Momsen'),
(15, 'Sam Worthington'), (15, 'Zoe Saldaña'), (15, 'Kate Winslet'),
(18, 'Jackie Chan'), (18, 'Lay Zhang');

-- ============================================
-- SHOWTIMES (Dec 7-21, 2025)
-- ============================================
-- NOW_PLAYING: Películas 1-10 del 7 al 21 de diciembre en los 8 cines
-- PRE_SALE: Películas 11-15 en fechas específicas

-- ESTRATEGIA DE DISTRIBUCIÓN:
-- - Cada película se proyecta 3-4 veces al día en cada cine
-- - Horarios: mañana (11:00-14:00), tarde (15:00-18:00), noche (19:00-22:00), trasnoche (22:30-00:00)
-- - Se distribuyen en diferentes salas según formato disponible (2D, 3D, XD)
-- - Se evitan cruces de horario en la misma sala considerando duración + 15min limpieza

-- ========== DÍA 1: 2025-12-07 ==========
-- Cinema 1 (Trujillo) - Theaters 1-12
-- Cinema 2 (Angamos) - Theaters 13-24
-- Cinema 3 (Arequipa) - Theaters 25-36
-- Cinema 4 (Asia) - Theaters 37-48
-- Cinema 5 (Bellavista) - Theaters 49-60
-- Cinema 6 (Gamarra) - Theaters 61-72
-- Cinema 7 (Jockey Plaza) - Theaters 73-84
-- Cinema 8 (Lambra) - Theaters 85-96
INSERT INTO showtimes (movie_id, theater_id, date, time, format, available_seats, price) VALUES
(1, 7, '2025-12-07', '11:00:00', '_2D', 168, 8.50), (1, 9, '2025-12-07', '15:00:00', '_2D', 120, 8.50), (1, 11, '2025-12-07', '19:30:00', '_2D', 80, 8.50),
(2, 1, '2025-12-07', '12:00:00', 'XD', 180, 14.00), (2, 3, '2025-12-07', '16:00:00', 'XD', 120, 14.00), (2, 7, '2025-12-07', '20:00:00', '_2D', 168, 8.50),
(3, 8, '2025-12-07', '13:00:00', '_2D', 168, 8.50), (3, 10, '2025-12-07', '17:00:00', '_2D', 120, 8.50), (3, 12, '2025-12-07', '21:00:00', '_2D', 80, 8.50),
(4, 9, '2025-12-07', '12:00:00', '_2D', 120, 8.50), (4, 11, '2025-12-07', '16:30:00', '_2D', 80, 8.50),
(5, 1, '2025-12-07', '18:00:00', 'XD', 180, 14.00), (5, 4, '2025-12-07', '14:00:00', '_3D', 120, 12.00), (5, 7, '2025-12-07', '22:00:00', '_2D', 168, 8.50),
(6, 10, '2025-12-07', '11:00:00', '_2D', 120, 8.50), (6, 12, '2025-12-07', '15:00:00', '_2D', 80, 8.50), (6, 8, '2025-12-07', '19:00:00', '_2D', 168, 8.50),
(7, 9, '2025-12-07', '18:00:00', '_2D', 120, 8.50), (7, 11, '2025-12-07', '22:00:00', '_2D', 80, 8.50),
(8, 2, '2025-12-07', '11:30:00', 'XD', 180, 14.00), (8, 3, '2025-12-07', '13:30:00', 'XD', 120, 14.00), (8, 8, '2025-12-07', '15:30:00', '_2D', 168, 8.50),
(9, 10, '2025-12-07', '19:30:00', '_2D', 120, 8.50), (9, 12, '2025-12-07', '17:30:00', '_2D', 80, 8.50),
(10, 2, '2025-12-07', '13:30:00', 'XD', 180, 14.00), (10, 5, '2025-12-07', '11:30:00', '_3D', 120, 12.00), (10, 6, '2025-12-07', '16:00:00', '_3D', 80, 12.00),
(1, 19, '2025-12-07', '11:30:00', '_2D', 168, 8.50), (1, 21, '2025-12-07', '15:30:00', '_2D', 120, 8.50),
(2, 13, '2025-12-07', '12:30:00', 'XD', 180, 14.00), (2, 15, '2025-12-07', '17:00:00', 'XD', 120, 14.00), (2, 19, '2025-12-07', '20:30:00', '_2D', 168, 8.50),
(3, 20, '2025-12-07', '13:30:00', '_2D', 168, 8.50), (3, 22, '2025-12-07', '17:30:00', '_2D', 120, 8.50),
(4, 21, '2025-12-07', '12:30:00', '_2D', 120, 8.50), (4, 23, '2025-12-07', '16:00:00', '_2D', 80, 8.50),
(5, 13, '2025-12-07', '18:30:00', 'XD', 180, 14.00), (5, 16, '2025-12-07', '14:30:00', '_3D', 120, 12.00), (5, 19, '2025-12-07', '22:30:00', '_2D', 168, 8.50),
(6, 22, '2025-12-07', '11:30:00', '_2D', 120, 8.50), (6, 24, '2025-12-07', '15:30:00', '_2D', 80, 8.50),
(7, 21, '2025-12-07', '18:30:00', '_2D', 120, 8.50), (7, 23, '2025-12-07', '22:00:00', '_2D', 80, 8.50),
(8, 14, '2025-12-07', '12:00:00', 'XD', 180, 14.00), (8, 20, '2025-12-07', '16:00:00', '_2D', 168, 8.50),
(9, 22, '2025-12-07', '19:30:00', '_2D', 120, 8.50),
(10, 14, '2025-12-07', '14:00:00', 'XD', 180, 14.00), (10, 17, '2025-12-07', '12:00:00', '_3D', 120, 12.00),
(1, 31, '2025-12-07', '12:00:00', '_2D', 168, 8.50), (1, 33, '2025-12-07', '16:00:00', '_2D', 120, 8.50),
(2, 25, '2025-12-07', '13:00:00', 'XD', 180, 14.00), (2, 27, '2025-12-07', '18:00:00', 'XD', 120, 14.00),
(3, 32, '2025-12-07', '14:00:00', '_2D', 168, 8.50), (3, 34, '2025-12-07', '18:00:00', '_2D', 120, 8.50),
(5, 25, '2025-12-07', '19:00:00', 'XD', 180, 14.00), (5, 28, '2025-12-07', '15:00:00', '_3D', 120, 12.00),
(10, 26, '2025-12-07', '11:00:00', 'XD', 180, 14.00), (10, 29, '2025-12-07', '13:00:00', '_3D', 120, 12.00),
(1, 43, '2025-12-07', '12:30:00', '_2D', 168, 8.50), (1, 45, '2025-12-07', '16:30:00', '_2D', 120, 8.50),
(2, 37, '2025-12-07', '13:30:00', 'XD', 180, 14.00), (2, 39, '2025-12-07', '18:30:00', 'XD', 120, 14.00),
(5, 37, '2025-12-07', '19:30:00', 'XD', 180, 14.00), (5, 40, '2025-12-07', '15:30:00', '_3D', 120, 12.00),
(10, 38, '2025-12-07', '11:30:00', 'XD', 180, 14.00), (10, 41, '2025-12-07', '13:30:00', '_3D', 120, 12.00),
(1, 55, '2025-12-07', '13:00:00', '_2D', 168, 8.50), (1, 57, '2025-12-07', '17:00:00', '_2D', 120, 8.50),
(2, 49, '2025-12-07', '14:00:00', 'XD', 180, 14.00), (2, 51, '2025-12-07', '19:00:00', 'XD', 120, 14.00),
(5, 49, '2025-12-07', '20:00:00', 'XD', 180, 14.00), (5, 52, '2025-12-07', '16:00:00', '_3D', 120, 12.00),
(10, 50, '2025-12-07', '12:00:00', 'XD', 180, 14.00), (10, 53, '2025-12-07', '14:00:00', '_3D', 120, 12.00),
(1, 67, '2025-12-07', '13:30:00', '_2D', 168, 8.50), (1, 69, '2025-12-07', '17:30:00', '_2D', 120, 8.50),
(2, 61, '2025-12-07', '14:30:00', 'XD', 180, 14.00), (2, 63, '2025-12-07', '19:30:00', 'XD', 120, 14.00),
(5, 61, '2025-12-07', '20:30:00', 'XD', 180, 14.00), (5, 64, '2025-12-07', '16:30:00', '_3D', 120, 12.00),
(10, 62, '2025-12-07', '12:30:00', 'XD', 180, 14.00), (10, 65, '2025-12-07', '14:30:00', '_3D', 120, 12.00),
(1, 79, '2025-12-07', '14:00:00', '_2D', 168, 8.50), (1, 81, '2025-12-07', '18:00:00', '_2D', 120, 8.50),
(2, 73, '2025-12-07', '15:00:00', 'XD', 180, 14.00), (2, 75, '2025-12-07', '20:00:00', 'XD', 120, 14.00),
(5, 73, '2025-12-07', '21:00:00', 'XD', 180, 14.00), (5, 76, '2025-12-07', '17:00:00', '_3D', 120, 12.00),
(10, 74, '2025-12-07', '13:00:00', 'XD', 180, 14.00), (10, 77, '2025-12-07', '15:00:00', '_3D', 120, 12.00),
(1, 91, '2025-12-07', '14:30:00', '_2D', 168, 8.50), (1, 93, '2025-12-07', '18:30:00', '_2D', 120, 8.50),
(2, 85, '2025-12-07', '15:30:00', 'XD', 180, 14.00), (2, 87, '2025-12-07', '20:30:00', 'XD', 120, 14.00),
(5, 85, '2025-12-07', '21:30:00', 'XD', 180, 14.00), (5, 88, '2025-12-07', '17:30:00', '_3D', 120, 12.00),
(10, 86, '2025-12-07', '13:30:00', 'XD', 180, 14.00), (10, 89, '2025-12-07', '15:30:00', '_3D', 120, 12.00);

-- ========== DÍA 8: 2025-12-08 ==========
-- Cinema 1 (Trujillo) - Theaters 1-12
-- Cinema 2 (Angamos) - Theaters 13-24
-- Cinema 3 (Arequipa) - Theaters 25-36
-- Cinema 4 (Asia) - Theaters 37-48
-- Cinema 5 (Bellavista) - Theaters 49-60
-- Cinema 6 (Gamarra) - Theaters 61-72
-- Cinema 7 (Jockey Plaza) - Theaters 73-84
-- Cinema 8 (Lambra) - Theaters 85-96
INSERT INTO showtimes (movie_id, theater_id, date, time, format, available_seats, price) VALUES
(1, 7, '2025-12-08', '11:00:00', '_2D', 168, 8.50), (1, 9, '2025-12-08', '15:00:00', '_2D', 120, 8.50), (1, 11, '2025-12-08', '19:30:00', '_2D', 80, 8.50),
(2, 1, '2025-12-08', '12:00:00', 'XD', 180, 14.00), (2, 3, '2025-12-08', '16:00:00', 'XD', 120, 14.00), (2, 7, '2025-12-08', '20:00:00', '_2D', 168, 8.50),
(3, 8, '2025-12-08', '13:00:00', '_2D', 168, 8.50), (3, 10, '2025-12-08', '17:00:00', '_2D', 120, 8.50), (3, 12, '2025-12-08', '21:00:00', '_2D', 80, 8.50),
(4, 9, '2025-12-08', '12:00:00', '_2D', 120, 8.50), (4, 11, '2025-12-08', '16:30:00', '_2D', 80, 8.50),
(5, 1, '2025-12-08', '18:00:00', 'XD', 180, 14.00), (5, 4, '2025-12-08', '14:00:00', '_3D', 120, 12.00), (5, 7, '2025-12-08', '22:00:00', '_2D', 168, 8.50),
(6, 10, '2025-12-08', '11:00:00', '_2D', 120, 8.50), (6, 12, '2025-12-08', '15:00:00', '_2D', 80, 8.50), (6, 8, '2025-12-08', '19:00:00', '_2D', 168, 8.50),
(7, 9, '2025-12-08', '18:00:00', '_2D', 120, 8.50), (7, 11, '2025-12-08', '22:00:00', '_2D', 80, 8.50),
(8, 2, '2025-12-08', '11:30:00', 'XD', 180, 14.00), (8, 3, '2025-12-08', '13:30:00', 'XD', 120, 14.00), (8, 8, '2025-12-08', '15:30:00', '_2D', 168, 8.50),
(9, 10, '2025-12-08', '19:30:00', '_2D', 120, 8.50), (9, 12, '2025-12-08', '17:30:00', '_2D', 80, 8.50),
(10, 2, '2025-12-08', '13:30:00', 'XD', 180, 14.00), (10, 5, '2025-12-08', '11:30:00', '_3D', 120, 12.00), (10, 6, '2025-12-08', '16:00:00', '_3D', 80, 12.00),
(1, 19, '2025-12-08', '11:30:00', '_2D', 168, 8.50), (1, 21, '2025-12-08', '15:30:00', '_2D', 120, 8.50),
(2, 13, '2025-12-08', '12:30:00', 'XD', 180, 14.00), (2, 15, '2025-12-08', '17:00:00', 'XD', 120, 14.00), (2, 19, '2025-12-08', '20:30:00', '_2D', 168, 8.50),
(3, 20, '2025-12-08', '13:30:00', '_2D', 168, 8.50), (3, 22, '2025-12-08', '17:30:00', '_2D', 120, 8.50),
(5, 13, '2025-12-08', '18:30:00', 'XD', 180, 14.00), (5, 16, '2025-12-08', '14:30:00', '_3D', 120, 12.00),
(10, 14, '2025-12-08', '14:00:00', 'XD', 180, 14.00), (10, 17, '2025-12-08', '12:00:00', '_3D', 120, 12.00),
(1, 31, '2025-12-08', '12:00:00', '_2D', 168, 8.50), (2, 25, '2025-12-08', '13:00:00', 'XD', 180, 14.00),
(5, 25, '2025-12-08', '19:00:00', 'XD', 180, 14.00), (10, 26, '2025-12-08', '11:00:00', 'XD', 180, 14.00),
(1, 43, '2025-12-08', '12:30:00', '_2D', 168, 8.50), (2, 37, '2025-12-08', '13:30:00', 'XD', 180, 14.00),
(5, 37, '2025-12-08', '19:30:00', 'XD', 180, 14.00), (10, 38, '2025-12-08', '11:30:00', 'XD', 180, 14.00),
(1, 55, '2025-12-08', '13:00:00', '_2D', 168, 8.50), (2, 49, '2025-12-08', '14:00:00', 'XD', 180, 14.00),
(5, 49, '2025-12-08', '20:00:00', 'XD', 180, 14.00), (10, 50, '2025-12-08', '12:00:00', 'XD', 180, 14.00),
(1, 67, '2025-12-08', '13:30:00', '_2D', 168, 8.50), (2, 61, '2025-12-08', '14:30:00', 'XD', 180, 14.00),
(5, 61, '2025-12-08', '20:30:00', 'XD', 180, 14.00), (10, 62, '2025-12-08', '12:30:00', 'XD', 180, 14.00),
(1, 79, '2025-12-08', '14:00:00', '_2D', 168, 8.50), (2, 73, '2025-12-08', '15:00:00', 'XD', 180, 14.00),
(5, 73, '2025-12-08', '21:00:00', 'XD', 180, 14.00), (10, 74, '2025-12-08', '13:00:00', 'XD', 180, 14.00),
(1, 91, '2025-12-08', '14:30:00', '_2D', 168, 8.50), (2, 85, '2025-12-08', '15:30:00', 'XD', 180, 14.00),
(5, 85, '2025-12-08', '21:30:00', 'XD', 180, 14.00), (10, 86, '2025-12-08', '13:30:00', 'XD', 180, 14.00);

-- ========== DÍA 9: 2025-12-09 ==========
-- Cinema 1
-- Cinema 2
-- Cinema 3
-- Cinema 4
-- Cinema 5
-- Cinema 6
-- Cinema 7
-- Cinema 8
INSERT INTO showtimes (movie_id, theater_id, date, time, format, available_seats, price) VALUES
(1, 7, '2025-12-09', '11:00:00', '_2D', 168, 8.50), (1, 9, '2025-12-09', '15:00:00', '_2D', 120, 8.50), (1, 11, '2025-12-09', '19:30:00', '_2D', 80, 8.50),
(2, 1, '2025-12-09', '12:00:00', 'XD', 180, 14.00), (2, 3, '2025-12-09', '16:00:00', 'XD', 120, 14.00), (2, 7, '2025-12-09', '20:00:00', '_2D', 168, 8.50),
(3, 8, '2025-12-09', '13:00:00', '_2D', 168, 8.50), (3, 10, '2025-12-09', '17:00:00', '_2D', 120, 8.50),
(4, 9, '2025-12-09', '12:00:00', '_2D', 120, 8.50), (4, 11, '2025-12-09', '16:30:00', '_2D', 80, 8.50),
(5, 1, '2025-12-09', '18:00:00', 'XD', 180, 14.00), (5, 4, '2025-12-09', '14:00:00', '_3D', 120, 12.00), (5, 7, '2025-12-09', '22:00:00', '_2D', 168, 8.50),
(6, 10, '2025-12-09', '11:00:00', '_2D', 120, 8.50), (6, 8, '2025-12-09', '19:00:00', '_2D', 168, 8.50),
(7, 9, '2025-12-09', '18:00:00', '_2D', 120, 8.50),
(8, 2, '2025-12-09', '11:30:00', 'XD', 180, 14.00), (8, 8, '2025-12-09', '15:30:00', '_2D', 168, 8.50),
(9, 10, '2025-12-09', '19:30:00', '_2D', 120, 8.50),
(10, 2, '2025-12-09', '13:30:00', 'XD', 180, 14.00), (10, 5, '2025-12-09', '11:30:00', '_3D', 120, 12.00),
(1, 19, '2025-12-09', '11:30:00', '_2D', 168, 8.50), (2, 13, '2025-12-09', '12:30:00', 'XD', 180, 14.00),
(5, 13, '2025-12-09', '18:30:00', 'XD', 180, 14.00), (10, 14, '2025-12-09', '14:00:00', 'XD', 180, 14.00),
(1, 31, '2025-12-09', '12:00:00', '_2D', 168, 8.50), (2, 25, '2025-12-09', '13:00:00', 'XD', 180, 14.00),
(5, 25, '2025-12-09', '19:00:00', 'XD', 180, 14.00), (10, 26, '2025-12-09', '11:00:00', 'XD', 180, 14.00),
(1, 43, '2025-12-09', '12:30:00', '_2D', 168, 8.50), (2, 37, '2025-12-09', '13:30:00', 'XD', 180, 14.00),
(5, 37, '2025-12-09', '19:30:00', 'XD', 180, 14.00), (10, 38, '2025-12-09', '11:30:00', 'XD', 180, 14.00),
(1, 55, '2025-12-09', '13:00:00', '_2D', 168, 8.50), (2, 49, '2025-12-09', '14:00:00', 'XD', 180, 14.00),
(5, 49, '2025-12-09', '20:00:00', 'XD', 180, 14.00), (10, 50, '2025-12-09', '12:00:00', 'XD', 180, 14.00),
(1, 67, '2025-12-09', '13:30:00', '_2D', 168, 8.50), (2, 61, '2025-12-09', '14:30:00', 'XD', 180, 14.00),
(5, 61, '2025-12-09', '20:30:00', 'XD', 180, 14.00), (10, 62, '2025-12-09', '12:30:00', 'XD', 180, 14.00),
(1, 79, '2025-12-09', '14:00:00', '_2D', 168, 8.50), (2, 73, '2025-12-09', '15:00:00', 'XD', 180, 14.00),
(5, 73, '2025-12-09', '21:00:00', 'XD', 180, 14.00), (10, 74, '2025-12-09', '13:00:00', 'XD', 180, 14.00),
(1, 91, '2025-12-09', '14:30:00', '_2D', 168, 8.50), (2, 85, '2025-12-09', '15:30:00', 'XD', 180, 14.00),
(5, 85, '2025-12-09', '21:30:00', 'XD', 180, 14.00), (10, 86, '2025-12-09', '13:30:00', 'XD', 180, 14.00);

-- ========== DÍA 10: 2025-12-10 ==========
-- Cinema 1
-- Cinema 2
-- Cinema 3
-- Cinema 4
-- Cinema 5
-- Cinema 6
-- Cinema 7
-- Cinema 8
INSERT INTO showtimes (movie_id, theater_id, date, time, format, available_seats, price) VALUES
(1, 7, '2025-12-10', '11:00:00', '_2D', 168, 8.50), (1, 9, '2025-12-10', '15:00:00', '_2D', 120, 8.50),
(2, 1, '2025-12-10', '12:00:00', 'XD', 180, 14.00), (2, 3, '2025-12-10', '16:00:00', 'XD', 120, 14.00),
(3, 8, '2025-12-10', '13:00:00', '_2D', 168, 8.50), (3, 10, '2025-12-10', '17:00:00', '_2D', 120, 8.50),
(5, 1, '2025-12-10', '18:00:00', 'XD', 180, 14.00), (5, 4, '2025-12-10', '14:00:00', '_3D', 120, 12.00),
(10, 2, '2025-12-10', '13:30:00', 'XD', 180, 14.00), (10, 5, '2025-12-10', '11:30:00', '_3D', 120, 12.00),
(1, 19, '2025-12-10', '11:30:00', '_2D', 168, 8.50), (2, 13, '2025-12-10', '12:30:00', 'XD', 180, 14.00),
(5, 13, '2025-12-10', '18:30:00', 'XD', 180, 14.00), (10, 14, '2025-12-10', '14:00:00', 'XD', 180, 14.00),
(1, 31, '2025-12-10', '12:00:00', '_2D', 168, 8.50), (2, 25, '2025-12-10', '13:00:00', 'XD', 180, 14.00),
(5, 25, '2025-12-10', '19:00:00', 'XD', 180, 14.00), (10, 26, '2025-12-10', '11:00:00', 'XD', 180, 14.00),
(1, 43, '2025-12-10', '12:30:00', '_2D', 168, 8.50), (2, 37, '2025-12-10', '13:30:00', 'XD', 180, 14.00),
(5, 37, '2025-12-10', '19:30:00', 'XD', 180, 14.00), (10, 38, '2025-12-10', '11:30:00', 'XD', 180, 14.00),
(1, 55, '2025-12-10', '13:00:00', '_2D', 168, 8.50), (2, 49, '2025-12-10', '14:00:00', 'XD', 180, 14.00),
(5, 49, '2025-12-10', '20:00:00', 'XD', 180, 14.00), (10, 50, '2025-12-10', '12:00:00', 'XD', 180, 14.00),
(1, 67, '2025-12-10', '13:30:00', '_2D', 168, 8.50), (2, 61, '2025-12-10', '14:30:00', 'XD', 180, 14.00),
(5, 61, '2025-12-10', '20:30:00', 'XD', 180, 14.00), (10, 62, '2025-12-10', '12:30:00', 'XD', 180, 14.00),
(1, 79, '2025-12-10', '14:00:00', '_2D', 168, 8.50), (2, 73, '2025-12-10', '15:00:00', 'XD', 180, 14.00),
(5, 73, '2025-12-10', '21:00:00', 'XD', 180, 14.00), (10, 74, '2025-12-10', '13:00:00', 'XD', 180, 14.00),
(1, 91, '2025-12-10', '14:30:00', '_2D', 168, 8.50), (2, 85, '2025-12-10', '15:30:00', 'XD', 180, 14.00),
(5, 85, '2025-12-10', '21:30:00', 'XD', 180, 14.00), (10, 86, '2025-12-10', '13:30:00', 'XD', 180, 14.00);

-- ========== DÍA 11: 2025-12-11 ==========
INSERT INTO showtimes (movie_id, theater_id, date, time, format, available_seats, price) VALUES
(1, 7, '2025-12-11', '11:00:00', '_2D', 168, 8.50), (1, 9, '2025-12-11', '15:00:00', '_2D', 120, 8.50),
(2, 1, '2025-12-11', '12:00:00', 'XD', 180, 14.00), (2, 3, '2025-12-11', '16:00:00', 'XD', 120, 14.00),
(3, 8, '2025-12-11', '13:00:00', '_2D', 168, 8.50), (5, 1, '2025-12-11', '18:00:00', 'XD', 180, 14.00),
(10, 2, '2025-12-11', '13:30:00', 'XD', 180, 14.00), (10, 5, '2025-12-11', '11:30:00', '_3D', 120, 12.00),
(1, 19, '2025-12-11', '11:30:00', '_2D', 168, 8.50), (2, 13, '2025-12-11', '12:30:00', 'XD', 180, 14.00),
(5, 13, '2025-12-11', '18:30:00', 'XD', 180, 14.00), (10, 14, '2025-12-11', '14:00:00', 'XD', 180, 14.00);

-- ========== DÍA 12: 2025-12-12 ==========
INSERT INTO showtimes (movie_id, theater_id, date, time, format, available_seats, price) VALUES
(1, 7, '2025-12-12', '11:00:00', '_2D', 168, 8.50), (1, 9, '2025-12-12', '15:00:00', '_2D', 120, 8.50),
(2, 1, '2025-12-12', '12:00:00', 'XD', 180, 14.00), (2, 3, '2025-12-12', '16:00:00', 'XD', 120, 14.00),
(3, 8, '2025-12-12', '13:00:00', '_2D', 168, 8.50), (5, 1, '2025-12-12', '18:00:00', 'XD', 180, 14.00),
(10, 2, '2025-12-12', '13:30:00', 'XD', 180, 14.00), (10, 5, '2025-12-12', '11:30:00', '_3D', 120, 12.00),
(1, 19, '2025-12-12', '11:30:00', '_2D', 168, 8.50), (2, 13, '2025-12-12', '12:30:00', 'XD', 180, 14.00),
(5, 13, '2025-12-12', '18:30:00', 'XD', 180, 14.00), (10, 14, '2025-12-12', '14:00:00', 'XD', 180, 14.00);

-- ========== DÍA 13: 2025-12-13 ==========
INSERT INTO showtimes (movie_id, theater_id, date, time, format, available_seats, price) VALUES
(1, 7, '2025-12-13', '11:00:00', '_2D', 168, 8.50), (1, 9, '2025-12-13', '15:00:00', '_2D', 120, 8.50),
(2, 1, '2025-12-13', '12:00:00', 'XD', 180, 14.00), (2, 3, '2025-12-13', '16:00:00', 'XD', 120, 14.00),
(3, 8, '2025-12-13', '13:00:00', '_2D', 168, 8.50), (5, 1, '2025-12-13', '18:00:00', 'XD', 180, 14.00),
(10, 2, '2025-12-13', '13:30:00', 'XD', 180, 14.00), (10, 5, '2025-12-13', '11:30:00', '_3D', 120, 12.00),
(1, 31, '2025-12-13', '12:00:00', '_2D', 168, 8.50), (2, 25, '2025-12-13', '13:00:00', 'XD', 180, 14.00),
(5, 25, '2025-12-13', '19:00:00', 'XD', 180, 14.00), (10, 26, '2025-12-13', '11:00:00', 'XD', 180, 14.00);

-- ========== DÍA 14: 2025-12-14 (Sábado - Mayor capacidad) ==========
INSERT INTO showtimes (movie_id, theater_id, date, time, format, available_seats, price) VALUES
(1, 7, '2025-12-14', '11:00:00', '_2D', 168, 8.50), (1, 9, '2025-12-14', '15:00:00', '_2D', 120, 8.50), (1, 11, '2025-12-14', '19:30:00', '_2D', 80, 8.50),
(2, 1, '2025-12-14', '12:00:00', 'XD', 180, 14.00), (2, 3, '2025-12-14', '16:00:00', 'XD', 120, 14.00), (2, 7, '2025-12-14', '20:00:00', '_2D', 168, 8.50),
(3, 8, '2025-12-14', '13:00:00', '_2D', 168, 8.50), (3, 10, '2025-12-14', '17:00:00', '_2D', 120, 8.50),
(5, 1, '2025-12-14', '18:00:00', 'XD', 180, 14.00), (5, 4, '2025-12-14', '14:00:00', '_3D', 120, 12.00), (5, 7, '2025-12-14', '22:00:00', '_2D', 168, 8.50),
(10, 2, '2025-12-14', '13:30:00', 'XD', 180, 14.00), (10, 5, '2025-12-14', '11:30:00', '_3D', 120, 12.00), (10, 6, '2025-12-14', '16:00:00', '_3D', 80, 12.00),
(1, 19, '2025-12-14', '11:30:00', '_2D', 168, 8.50), (2, 13, '2025-12-14', '12:30:00', 'XD', 180, 14.00),
(5, 13, '2025-12-14', '18:30:00', 'XD', 180, 14.00), (10, 14, '2025-12-14', '14:00:00', 'XD', 180, 14.00);

-- ========== DÍA 15: 2025-12-15 (Domingo - Mayor capacidad) ==========
INSERT INTO showtimes (movie_id, theater_id, date, time, format, available_seats, price) VALUES
(1, 7, '2025-12-15', '11:00:00', '_2D', 168, 8.50), (1, 9, '2025-12-15', '15:00:00', '_2D', 120, 8.50), (1, 11, '2025-12-15', '19:30:00', '_2D', 80, 8.50),
(2, 1, '2025-12-15', '12:00:00', 'XD', 180, 14.00), (2, 3, '2025-12-15', '16:00:00', 'XD', 120, 14.00), (2, 7, '2025-12-15', '20:00:00', '_2D', 168, 8.50),
(3, 8, '2025-12-15', '13:00:00', '_2D', 168, 8.50), (3, 10, '2025-12-15', '17:00:00', '_2D', 120, 8.50),
(5, 1, '2025-12-15', '18:00:00', 'XD', 180, 14.00), (5, 4, '2025-12-15', '14:00:00', '_3D', 120, 12.00), (5, 7, '2025-12-15', '22:00:00', '_2D', 168, 8.50),
(10, 2, '2025-12-15', '13:30:00', 'XD', 180, 14.00), (10, 5, '2025-12-15', '11:30:00', '_3D', 120, 12.00), (10, 6, '2025-12-15', '16:00:00', '_3D', 80, 12.00),
(1, 19, '2025-12-15', '11:30:00', '_2D', 168, 8.50), (2, 13, '2025-12-15', '12:30:00', 'XD', 180, 14.00),
(5, 13, '2025-12-15', '18:30:00', 'XD', 180, 14.00), (10, 14, '2025-12-15', '14:00:00', 'XD', 180, 14.00);

-- ========== DÍA 16: 2025-12-16 ==========
INSERT INTO showtimes (movie_id, theater_id, date, time, format, available_seats, price) VALUES
(1, 7, '2025-12-16', '11:00:00', '_2D', 168, 8.50), (1, 9, '2025-12-16', '15:00:00', '_2D', 120, 8.50),
(2, 1, '2025-12-16', '12:00:00', 'XD', 180, 14.00), (2, 3, '2025-12-16', '16:00:00', 'XD', 120, 14.00),
(3, 8, '2025-12-16', '13:00:00', '_2D', 168, 8.50), (5, 1, '2025-12-16', '18:00:00', 'XD', 180, 14.00),
(10, 2, '2025-12-16', '13:30:00', 'XD', 180, 14.00), (10, 5, '2025-12-16', '11:30:00', '_3D', 120, 12.00),
(1, 43, '2025-12-16', '12:30:00', '_2D', 168, 8.50), (2, 37, '2025-12-16', '13:30:00', 'XD', 180, 14.00),
(5, 37, '2025-12-16', '19:30:00', 'XD', 180, 14.00), (10, 38, '2025-12-16', '11:30:00', 'XD', 180, 14.00);

-- ========== DÍA 17: 2025-12-17 ==========
INSERT INTO showtimes (movie_id, theater_id, date, time, format, available_seats, price) VALUES
(1, 7, '2025-12-17', '11:00:00', '_2D', 168, 8.50), (1, 9, '2025-12-17', '15:00:00', '_2D', 120, 8.50),
(2, 1, '2025-12-17', '12:00:00', 'XD', 180, 14.00), (2, 3, '2025-12-17', '16:00:00', 'XD', 120, 14.00),
(3, 8, '2025-12-17', '13:00:00', '_2D', 168, 8.50), (5, 1, '2025-12-17', '18:00:00', 'XD', 180, 14.00),
(10, 2, '2025-12-17', '13:30:00', 'XD', 180, 14.00), (10, 5, '2025-12-17', '11:30:00', '_3D', 120, 12.00),
(1, 55, '2025-12-17', '13:00:00', '_2D', 168, 8.50), (2, 49, '2025-12-17', '14:00:00', 'XD', 180, 14.00),
(5, 49, '2025-12-17', '20:00:00', 'XD', 180, 14.00), (10, 50, '2025-12-17', '12:00:00', 'XD', 180, 14.00);

-- ========== DÍA 18: 2025-12-18 ==========
INSERT INTO showtimes (movie_id, theater_id, date, time, format, available_seats, price) VALUES
(1, 7, '2025-12-18', '11:00:00', '_2D', 168, 8.50), (1, 9, '2025-12-18', '15:00:00', '_2D', 120, 8.50),
(2, 1, '2025-12-18', '12:00:00', 'XD', 180, 14.00), (2, 3, '2025-12-18', '16:00:00', 'XD', 120, 14.00),
(3, 8, '2025-12-18', '13:00:00', '_2D', 168, 8.50), (5, 1, '2025-12-18', '18:00:00', 'XD', 180, 14.00),
(10, 2, '2025-12-18', '13:30:00', 'XD', 180, 14.00), (10, 5, '2025-12-18', '11:30:00', '_3D', 120, 12.00),
(1, 67, '2025-12-18', '13:30:00', '_2D', 168, 8.50), (2, 61, '2025-12-18', '14:30:00', 'XD', 180, 14.00),
(5, 61, '2025-12-18', '20:30:00', 'XD', 180, 14.00), (10, 62, '2025-12-18', '12:30:00', 'XD', 180, 14.00);

-- ========== DÍA 19: 2025-12-19 ==========
INSERT INTO showtimes (movie_id, theater_id, date, time, format, available_seats, price) VALUES
(1, 7, '2025-12-19', '11:00:00', '_2D', 168, 8.50), (1, 9, '2025-12-19', '15:00:00', '_2D', 120, 8.50),
(2, 1, '2025-12-19', '12:00:00', 'XD', 180, 14.00), (2, 3, '2025-12-19', '16:00:00', 'XD', 120, 14.00),
(3, 8, '2025-12-19', '13:00:00', '_2D', 168, 8.50), (5, 1, '2025-12-19', '18:00:00', 'XD', 180, 14.00),
(10, 2, '2025-12-19', '13:30:00', 'XD', 180, 14.00), (10, 5, '2025-12-19', '11:30:00', '_3D', 120, 12.00),
(1, 79, '2025-12-19', '14:00:00', '_2D', 168, 8.50), (2, 73, '2025-12-19', '15:00:00', 'XD', 180, 14.00),
(5, 73, '2025-12-19', '21:00:00', 'XD', 180, 14.00), (10, 74, '2025-12-19', '13:00:00', 'XD', 180, 14.00);

-- ========== DÍA 20: 2025-12-20 ==========
INSERT INTO showtimes (movie_id, theater_id, date, time, format, available_seats, price) VALUES
(1, 7, '2025-12-20', '11:00:00', '_2D', 168, 8.50), (1, 9, '2025-12-20', '15:00:00', '_2D', 120, 8.50),
(2, 1, '2025-12-20', '12:00:00', 'XD', 180, 14.00), (2, 3, '2025-12-20', '16:00:00', 'XD', 120, 14.00),
(3, 8, '2025-12-20', '13:00:00', '_2D', 168, 8.50), (5, 1, '2025-12-20', '18:00:00', 'XD', 180, 14.00),
(10, 2, '2025-12-20', '13:30:00', 'XD', 180, 14.00), (10, 5, '2025-12-20', '11:30:00', '_3D', 120, 12.00),
(1, 91, '2025-12-20', '14:30:00', '_2D', 168, 8.50), (2, 85, '2025-12-20', '15:30:00', 'XD', 180, 14.00),
(5, 85, '2025-12-20', '21:30:00', 'XD', 180, 14.00), (10, 86, '2025-12-20', '13:30:00', 'XD', 180, 14.00);

-- ========== DÍA 21: 2025-12-21 (Último día) ==========
INSERT INTO showtimes (movie_id, theater_id, date, time, format, available_seats, price) VALUES
(1, 7, '2025-12-21', '11:00:00', '_2D', 168, 8.50), (1, 9, '2025-12-21', '15:00:00', '_2D', 120, 8.50), (1, 11, '2025-12-21', '19:30:00', '_2D', 80, 8.50),
(2, 1, '2025-12-21', '12:00:00', 'XD', 180, 14.00), (2, 3, '2025-12-21', '16:00:00', 'XD', 120, 14.00), (2, 7, '2025-12-21', '20:00:00', '_2D', 168, 8.50),
(3, 8, '2025-12-21', '13:00:00', '_2D', 168, 8.50), (3, 10, '2025-12-21', '17:00:00', '_2D', 120, 8.50),
(4, 9, '2025-12-21', '12:00:00', '_2D', 120, 8.50), (4, 11, '2025-12-21', '16:30:00', '_2D', 80, 8.50),
(5, 1, '2025-12-21', '18:00:00', 'XD', 180, 14.00), (5, 4, '2025-12-21', '14:00:00', '_3D', 120, 12.00), (5, 7, '2025-12-21', '22:00:00', '_2D', 168, 8.50),
(6, 10, '2025-12-21', '11:00:00', '_2D', 120, 8.50), (6, 12, '2025-12-21', '15:00:00', '_2D', 80, 8.50),
(7, 9, '2025-12-21', '18:00:00', '_2D', 120, 8.50), (7, 11, '2025-12-21', '22:00:00', '_2D', 80, 8.50),
(8, 2, '2025-12-21', '11:30:00', 'XD', 180, 14.00), (8, 3, '2025-12-21', '13:30:00', 'XD', 120, 14.00),
(9, 10, '2025-12-21', '19:30:00', '_2D', 120, 8.50), (9, 12, '2025-12-21', '17:30:00', '_2D', 80, 8.50),
(10, 2, '2025-12-21', '13:30:00', 'XD', 180, 14.00), (10, 5, '2025-12-21', '11:30:00', '_3D', 120, 12.00), (10, 6, '2025-12-21', '16:00:00', '_3D', 80, 12.00),
(1, 19, '2025-12-21', '11:30:00', '_2D', 168, 8.50), (1, 21, '2025-12-21', '15:30:00', '_2D', 120, 8.50),
(2, 13, '2025-12-21', '12:30:00', 'XD', 180, 14.00), (2, 15, '2025-12-21', '17:00:00', 'XD', 120, 14.00),
(5, 13, '2025-12-21', '18:30:00', 'XD', 180, 14.00), (5, 16, '2025-12-21', '14:30:00', '_3D', 120, 12.00),
(10, 14, '2025-12-21', '14:00:00', 'XD', 180, 14.00), (10, 17, '2025-12-21', '12:00:00', '_3D', 120, 12.00),
(1, 31, '2025-12-21', '12:00:00', '_2D', 168, 8.50), (2, 25, '2025-12-21', '13:00:00', 'XD', 180, 14.00),
(5, 25, '2025-12-21', '19:00:00', 'XD', 180, 14.00), (10, 26, '2025-12-21', '11:00:00', 'XD', 180, 14.00),
(1, 43, '2025-12-21', '12:30:00', '_2D', 168, 8.50), (2, 37, '2025-12-21', '13:30:00', 'XD', 180, 14.00),
(5, 37, '2025-12-21', '19:30:00', 'XD', 180, 14.00), (10, 38, '2025-12-21', '11:30:00', 'XD', 180, 14.00),
(1, 55, '2025-12-21', '13:00:00', '_2D', 168, 8.50), (2, 49, '2025-12-21', '14:00:00', 'XD', 180, 14.00),
(5, 49, '2025-12-21', '20:00:00', 'XD', 180, 14.00), (10, 50, '2025-12-21', '12:00:00', 'XD', 180, 14.00),
(1, 67, '2025-12-21', '13:30:00', '_2D', 168, 8.50), (2, 61, '2025-12-21', '14:30:00', 'XD', 180, 14.00),
(5, 61, '2025-12-21', '20:30:00', 'XD', 180, 14.00), (10, 62, '2025-12-21', '12:30:00', 'XD', 180, 14.00),
(1, 79, '2025-12-21', '14:00:00', '_2D', 168, 8.50), (2, 73, '2025-12-21', '15:00:00', 'XD', 180, 14.00),
(5, 73, '2025-12-21', '21:00:00', 'XD', 180, 14.00), (10, 74, '2025-12-21', '13:00:00', 'XD', 180, 14.00),
(1, 91, '2025-12-21', '14:30:00', '_2D', 168, 8.50), (2, 85, '2025-12-21', '15:30:00', 'XD', 180, 14.00),
(5, 85, '2025-12-21', '21:30:00', 'XD', 180, 14.00), (10, 86, '2025-12-21', '13:30:00', 'XD', 180, 14.00);

-- ========== PRE_SALE SHOWTIMES ==========
-- Películas 11-15 con horarios nocturnos (21:45 y 23:45) en TODOS los 8 cines

-- PRE_SALE: Película 11 (Crítica Bugonia) - 10 dic - 2D en todos los cines
-- PRE_SALE: Película 12 (Amanecer Parte 1) - 11 dic - 2D en todos los cines
-- PRE_SALE: Película 13 (Amanecer Parte 2) - 11 dic - 2D en todos los cines
-- PRE_SALE: Película 14 (El Grinch) - 11 dic - 3D y 2D en todos los cines
-- PRE_SALE: Película 15 (Avatar 3) - 10 dic - 3D, 2D, XD en todos los cines
INSERT INTO showtimes (movie_id, theater_id, date, time, format, available_seats, price) VALUES
(11, 7, '2025-12-10', '21:45:00', '_2D', 168, 8.50), (11, 7, '2025-12-10', '23:45:00', '_2D', 168, 8.50),
(11, 19, '2025-12-10', '21:45:00', '_2D', 168, 8.50), (11, 19, '2025-12-10', '23:45:00', '_2D', 168, 8.50),
(11, 31, '2025-12-10', '21:45:00', '_2D', 168, 8.50), (11, 31, '2025-12-10', '23:45:00', '_2D', 168, 8.50),
(11, 43, '2025-12-10', '21:45:00', '_2D', 168, 8.50), (11, 43, '2025-12-10', '23:45:00', '_2D', 168, 8.50),
(11, 55, '2025-12-10', '21:45:00', '_2D', 168, 8.50), (11, 55, '2025-12-10', '23:45:00', '_2D', 168, 8.50),
(11, 67, '2025-12-10', '21:45:00', '_2D', 168, 8.50), (11, 67, '2025-12-10', '23:45:00', '_2D', 168, 8.50),
(11, 79, '2025-12-10', '21:45:00', '_2D', 168, 8.50), (11, 79, '2025-12-10', '23:45:00', '_2D', 168, 8.50),
(11, 91, '2025-12-10', '21:45:00', '_2D', 168, 8.50), (11, 91, '2025-12-10', '23:45:00', '_2D', 168, 8.50),
(12, 8, '2025-12-11', '21:45:00', '_2D', 168, 8.50), (12, 8, '2025-12-11', '23:45:00', '_2D', 168, 8.50),
(12, 20, '2025-12-11', '21:45:00', '_2D', 168, 8.50), (12, 20, '2025-12-11', '23:45:00', '_2D', 168, 8.50),
(12, 32, '2025-12-11', '21:45:00', '_2D', 168, 8.50), (12, 32, '2025-12-11', '23:45:00', '_2D', 168, 8.50),
(12, 44, '2025-12-11', '21:45:00', '_2D', 168, 8.50), (12, 44, '2025-12-11', '23:45:00', '_2D', 168, 8.50),
(12, 56, '2025-12-11', '21:45:00', '_2D', 168, 8.50), (12, 56, '2025-12-11', '23:45:00', '_2D', 168, 8.50),
(12, 68, '2025-12-11', '21:45:00', '_2D', 168, 8.50), (12, 68, '2025-12-11', '23:45:00', '_2D', 168, 8.50),
(12, 80, '2025-12-11', '21:45:00', '_2D', 168, 8.50), (12, 80, '2025-12-11', '23:45:00', '_2D', 168, 8.50),
(12, 92, '2025-12-11', '21:45:00', '_2D', 168, 8.50), (12, 92, '2025-12-11', '23:45:00', '_2D', 168, 8.50),
(13, 9, '2025-12-11', '21:45:00', '_2D', 120, 8.50), (13, 9, '2025-12-11', '23:45:00', '_2D', 120, 8.50),
(13, 21, '2025-12-11', '21:45:00', '_2D', 120, 8.50), (13, 21, '2025-12-11', '23:45:00', '_2D', 120, 8.50),
(13, 33, '2025-12-11', '21:45:00', '_2D', 120, 8.50), (13, 33, '2025-12-11', '23:45:00', '_2D', 120, 8.50),
(13, 45, '2025-12-11', '21:45:00', '_2D', 120, 8.50), (13, 45, '2025-12-11', '23:45:00', '_2D', 120, 8.50),
(13, 57, '2025-12-11', '21:45:00', '_2D', 120, 8.50), (13, 57, '2025-12-11', '23:45:00', '_2D', 120, 8.50),
(13, 69, '2025-12-11', '21:45:00', '_2D', 120, 8.50), (13, 69, '2025-12-11', '23:45:00', '_2D', 120, 8.50),
(13, 81, '2025-12-11', '21:45:00', '_2D', 120, 8.50), (13, 81, '2025-12-11', '23:45:00', '_2D', 120, 8.50),
(13, 93, '2025-12-11', '21:45:00', '_2D', 120, 8.50), (13, 93, '2025-12-11', '23:45:00', '_2D', 120, 8.50),
(14, 4, '2025-12-11', '21:45:00', '_3D', 120, 12.00), (14, 10, '2025-12-11', '23:45:00', '_2D', 120, 8.50),
(14, 16, '2025-12-11', '21:45:00', '_3D', 120, 12.00), (14, 22, '2025-12-11', '23:45:00', '_2D', 120, 8.50),
(14, 28, '2025-12-11', '21:45:00', '_3D', 120, 12.00), (14, 34, '2025-12-11', '23:45:00', '_2D', 120, 8.50),
(14, 40, '2025-12-11', '21:45:00', '_3D', 120, 12.00), (14, 46, '2025-12-11', '23:45:00', '_2D', 120, 8.50),
(14, 52, '2025-12-11', '21:45:00', '_3D', 120, 12.00), (14, 58, '2025-12-11', '23:45:00', '_2D', 120, 8.50),
(14, 64, '2025-12-11', '21:45:00', '_3D', 120, 12.00), (14, 70, '2025-12-11', '23:45:00', '_2D', 120, 8.50),
(14, 76, '2025-12-11', '21:45:00', '_3D', 120, 12.00), (14, 82, '2025-12-11', '23:45:00', '_2D', 120, 8.50),
(14, 88, '2025-12-11', '21:45:00', '_3D', 120, 12.00), (14, 94, '2025-12-11', '23:45:00', '_2D', 120, 8.50),
(15, 1, '2025-12-10', '21:45:00', 'XD', 180, 14.00), (15, 5, '2025-12-10', '23:45:00', '_3D', 120, 12.00),
(15, 13, '2025-12-10', '21:45:00', 'XD', 180, 14.00), (15, 17, '2025-12-10', '23:45:00', '_3D', 120, 12.00),
(15, 25, '2025-12-10', '21:45:00', 'XD', 180, 14.00), (15, 29, '2025-12-10', '23:45:00', '_3D', 120, 12.00),
(15, 37, '2025-12-10', '21:45:00', 'XD', 180, 14.00), (15, 41, '2025-12-10', '23:45:00', '_3D', 120, 12.00),
(15, 49, '2025-12-10', '21:45:00', 'XD', 180, 14.00), (15, 53, '2025-12-10', '23:45:00', '_3D', 120, 12.00),
(15, 61, '2025-12-10', '21:45:00', 'XD', 180, 14.00), (15, 65, '2025-12-10', '23:45:00', '_3D', 120, 12.00),
(15, 73, '2025-12-10', '21:45:00', 'XD', 180, 14.00), (15, 77, '2025-12-10', '23:45:00', '_3D', 120, 12.00),
(15, 85, '2025-12-10', '21:45:00', 'XD', 180, 14.00), (15, 89, '2025-12-10', '23:45:00', '_3D', 120, 12.00);

-- ============================================
-- VERIFICACIÓN
-- ============================================

SELECT 'Resumen de datos insertados:' as titulo;
SELECT 'roles' as tabla, COUNT(*) as total FROM roles
UNION ALL SELECT 'movies', COUNT(*) FROM movies
UNION ALL SELECT 'cinemas', COUNT(*) FROM cinemas
UNION ALL SELECT 'theaters', COUNT(*) FROM theaters
UNION ALL SELECT 'showtimes', COUNT(*) FROM showtimes
UNION ALL SELECT 'payment_methods', COUNT(*) FROM payment_methods
UNION ALL SELECT 'users', COUNT(*) FROM users;

-- Ver películas por estado
SELECT status, COUNT(*) as cantidad FROM movies GROUP BY status;

-- FIN DEL SCRIPT
