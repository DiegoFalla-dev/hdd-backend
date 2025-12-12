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
-- Solo inserta los que faltan (evita duplicados)

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

-- 2.1 Debe de estar en 0
SELECT COUNT(*) FROM cinema_available_formats;

-- 3. CONCESSION_PRODUCTS
INSERT INTO concession_products (name, description, price, category, image_url, available) VALUES
('Combo Caliente', 'Canchita grande + Gaseosa grande + Hot dog', 50.20, 'COMBOS', 'https://i.imgur.com/3QXTlTP.png', 1),
('Combo Sal', 'Canchita mediana + Gaseosa grande + Nachos', 48.30, 'COMBOS', 'https://i.imgur.com/mn8MkFr.png', 1),
('Combo Familiar', '4 Canchitas grandes + 3 Gaseosas grandes', 149.00, 'COMBOS', 'https://i.imgur.com/8mqvkF4.png', 1),
('Combo Dulce', 'Canchita grande dulce + Frugos + Nuggets x6', 47.40, 'COMBOS', 'https://i.imgur.com/E9ZykEX.png', 1),
('Combo Pareja', '2 Canchitas medianas + 2 Gaseosas grandes', 69.90, 'COMBOS', 'https://i.imgur.com/dkPseQ3.png', 1),
('Combo Burger Premium', 'Canchita gigante + 2 Gaseosas grandes + Hamburguesa', 35.90, 'COMBOS', 'https://i.imgur.com/4f5OE64.png', 1),
('Canchita Gigante Salada', 'Porción gigante de canchita con sal', 34.90, 'CANCHITA', 'https://i.imgur.com/6X0TjP9.png', 1),
('Canchita Grande Salada', 'Porción grande de canchita con sal', 29.90, 'CANCHITA', 'https://i.imgur.com/yoalqQt.png', 1),
('Canchita Grande Dulce', 'Porción grande de canchita dulce', 31.90, 'CANCHITA', 'https://i.imgur.com/4BCV5nd.png', 1),
('Canchita Mediana Salada', 'Porción mediana de canchita con sal', 26.90, 'CANCHITA', 'https://i.imgur.com/uJlVCc9.png', 1),
('Canchita Mediana Dulce', 'Porción mediana de canchita dulce', 27.90, 'CANCHITA', 'https://i.imgur.com/UCS4p8y.png', 1),
('Canchita Kids Salada', 'Porción Kids de canchita con sal', 11.90, 'CANCHITA', 'https://i.imgur.com/JBRSxds.png', 1),
('Canchita Kids Dulce', 'Canchita Kids dulce', 12.90, 'CANCHITA', 'https://i.imgur.com/Nu0Ow6U.png', 1),
('Canchita Mantequilla', 'Canchita grande con mantequilla', 31.90, 'CANCHITA', 'https://i.imgur.com/oSnb1b2.png', 1),
('Coca Cola Grande', 'Coca Cola 500ml', 11.90, 'BEBIDAS', 'https://i.imgur.com/H0od7eK.png', 1),
('Inca Kola Grande', 'Inca Kola 500ml', 11.90, 'BEBIDAS', 'https://i.imgur.com/nUkZc3u.png', 1),
('Sprite Grande', 'Sprite 500ml', 11.90, 'BEBIDAS', 'https://i.imgur.com/YrRj40A.png', 1),
('Jugo de Naranja', 'Jugo natural de naranja 400ml', 11.90, 'BEBIDAS', 'https://i.imgur.com/1gzH5cZ.png', 1),
('Frugos del valle', 'Frugos 300ml', 6.90, 'BEBIDAS', 'https://i.imgur.com/wPEQYeE.png', 1),
('Agua con Gas', 'Agua mineral con gas 500ml', 6.90, 'BEBIDAS', 'https://i.imgur.com/2S1tlC6.png', 1),
('Agua Sin Gas', 'Agua mineral 500ml', 5.90, 'BEBIDAS', 'https://i.imgur.com/kAjLTU3.png', 1),
('Hot Dog Frankfurter', 'Hot dog con salchicha alemana y salsas', 13.90, 'SNACKS', 'https://i.imgur.com/vgYqN6n.png', 1),
('Nachos con Queso', 'Nachos crujientes con salsa de queso', 14.90, 'SNACKS', 'https://i.imgur.com/fmBuiPG.png', 1),
('Papas Fritas', 'Papas fritas crujientes porción grande', 7.90, 'SNACKS', 'https://i.imgur.com/X5f8YC9.png', 1),
('Tequeños x4 un', '4 tequeños de queso fritos', 10.90, 'SNACKS', 'https://i.imgur.com/bDMNPBk.png', 1),
('Nuggets x6', '6 nuggets de pollo crujientes', 13.90, 'SNACKS', 'https://i.imgur.com/MtnELKD.png', 1),
('Salchipapas', 'Papas fritas con salchicha y salsas', 14.90, 'SNACKS', 'https://i.imgur.com/aF0NUuv.png', 1),
('Hamburguesa Clásica', 'Hamburguesa con carne, lechuga y tomate', 16.90, 'SNACKS', 'https://i.imgur.com/8U3R1Oa.png', 1),
('Pizza Personal', 'Pizza individual de pepperoni', 18.90, 'SNACKS', 'https://i.imgur.com/oMEqVGb.png', 1),
('Sandwich Club', 'Sandwich triple con pollo, tocino y verduras', 16.90, 'SNACKS', 'https://i.imgur.com/jwVxk1i.png', 1),
('Alitas BBQ x7', '7 alitas de pollo con salsa BBQ', 18.90, 'SNACKS', 'https://i.imgur.com/cXOORTE.png', 1),
('Quesadilla', 'Quesadilla de queso con guacamole', 15.90, 'SNACKS', 'https://i.imgur.com/oK4ZOoP.png', 1),
('Wrap de Pollo', 'Wrap con pollo, lechuga y salsa ranch', 19.90, 'SNACKS', 'https://i.imgur.com/VwpHY4O.png', 1);

-- Insertar todas las combinaciones de cinema_id (1-8) con product_id (1-33)
INSERT INTO cinema_product (cinema_id, product_id) VALUES
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6), (1, 7), (1, 8), (1, 9), (1, 10),
(1, 11), (1, 12), (1, 13), (1, 14), (1, 15), (1, 16), (1, 17), (1, 18), (1, 19), (1, 20),
(1, 21), (1, 22), (1, 23), (1, 24), (1, 25), (1, 26), (1, 27), (1, 28), (1, 29), (1, 30),
(1, 31), (1, 32), (1, 33),
(2, 1), (2, 2), (2, 3), (2, 4), (2, 5), (2, 6), (2, 7), (2, 8), (2, 9), (2, 10),
(2, 11), (2, 12), (2, 13), (2, 14), (2, 15), (2, 16), (2, 17), (2, 18), (2, 19), (2, 20),
(2, 21), (2, 22), (2, 23), (2, 24), (2, 25), (2, 26), (2, 27), (2, 28), (2, 29), (2, 30),
(2, 31), (2, 32), (2, 33),
(3, 1), (3, 2), (3, 3), (3, 4), (3, 5), (3, 6), (3, 7), (3, 8), (3, 9), (3, 10),
(3, 11), (3, 12), (3, 13), (3, 14), (3, 15), (3, 16), (3, 17), (3, 18), (3, 19), (3, 20),
(3, 21), (3, 22), (3, 23), (3, 24), (3, 25), (3, 26), (3, 27), (3, 28), (3, 29), (3, 30),
(3, 31), (3, 32), (3, 33),
(4, 1), (4, 2), (4, 3), (4, 4), (4, 5), (4, 6), (4, 7), (4, 8), (4, 9), (4, 10),
(4, 11), (4, 12), (4, 13), (4, 14), (4, 15), (4, 16), (4, 17), (4, 18), (4, 19), (4, 20),
(4, 21), (4, 22), (4, 23), (4, 24), (4, 25), (4, 26), (4, 27), (4, 28), (4, 29), (4, 30),
(4, 31), (4, 32), (4, 33),
(5, 1), (5, 2), (5, 3), (5, 4), (5, 5), (5, 6), (5, 7), (5, 8), (5, 9), (5, 10),
(5, 11), (5, 12), (5, 13), (5, 14), (5, 15), (5, 16), (5, 17), (5, 18), (5, 19), (5, 20),
(5, 21), (5, 22), (5, 23), (5, 24), (5, 25), (5, 26), (5, 27), (5, 28), (5, 29), (5, 30),
(5, 31), (5, 32), (5, 33),
(6, 1), (6, 2), (6, 3), (6, 4), (6, 5), (6, 6), (6, 7), (6, 8), (6, 9), (6, 10),
(6, 11), (6, 12), (6, 13), (6, 14), (6, 15), (6, 16), (6, 17), (6, 18), (6, 19), (6, 20),
(6, 21), (6, 22), (6, 23), (6, 24), (6, 25), (6, 26), (6, 27), (6, 28), (6, 29), (6, 30),
(6, 31), (6, 32), (6, 33),
(7, 1), (7, 2), (7, 3), (7, 4), (7, 5), (7, 6), (7, 7), (7, 8), (7, 9), (7, 10),
(7, 11), (7, 12), (7, 13), (7, 14), (7, 15), (7, 16), (7, 17), (7, 18), (7, 19), (7, 20),
(7, 21), (7, 22), (7, 23), (7, 24), (7, 25), (7, 26), (7, 27), (7, 28), (7, 29), (7, 30),
(7, 31), (7, 32), (7, 33),
(8, 1), (8, 2), (8, 3), (8, 4), (8, 5), (8, 6), (8, 7), (8, 8), (8, 9), (8, 10),
(8, 11), (8, 12), (8, 13), (8, 14), (8, 15), (8, 16), (8, 17), (8, 18), (8, 19), (8, 20),
(8, 21), (8, 22), (8, 23), (8, 24), (8, 25), (8, 26), (8, 27), (8, 28), (8, 29), (8, 30),
(8, 31), (8, 32), (8, 33);

-- 5. TICKET_TYPES
-- NOTA: Los ticket_types se crean automáticamente por DataLoader.java si la tabla está vacía.
-- Si ejecutas este INSERT manualmente, el DataLoader NO los creará.
-- Columnas: code, name, price, active (NO tiene 'description' ni 'is_active')
INSERT INTO ticket_types (code, name, price, active) VALUES
('PROMO_ONLINE', 'PROMO ONLINE', 14.96, 1),
('PERSONA_CON_DISCAPACIDAD', 'PERSONA CON DISCAPACIDAD', 17.70, 1),
('SILLA_DE_RUEDAS', 'SILLA DE RUEDAS', 17.70, 1),
('NINO', 'NIÑO', 21.60, 1),
('ADULTO', 'ADULTO', 23.60, 1),

-- ============================================
-- PASO 2: TABLAS CON 1 NIVEL DE DEPENDENCIA
-- ============================================

-- 7. USER_ROLES
-- NOTA: Los role_id se asignan automáticamente: 1=ROLE_ADMIN, 2=ROLE_MANAGER, 3=ROLE_USER
INSERT INTO user_roles (user_id, role_id) VALUES
(1, 1), -- admin tiene ROLE_ADMIN
(1, 2), -- admin tiene ROLE_MANAGER (múltiples roles)
(2, 3); -- diego123 tiene ROLE_USER

-- 8. THEATERS (96 salas total: 8 cines × 12 salas)
-- Cada cine: 3 XD (LARGE/LARGE/MEDIUM), 3 3D (MEDIUM/MEDIUM/SMALL), 6 2D (LARGE×2/MEDIUM×2/SMALL×2)
--INSERT INTO theaters (cinema_id, name, seat_matrix_type, row_count, col_count, total_seats) VALUES
-- Cinema 1: Trujillo (theater_id 1-12)
-- (1, 'Sala XD 1', 'LARGE', 12, 15, 180), (1, 'Sala XD 2', 'LARGE', 12, 15, 180), (1, 'Sala XD 3', 'MEDIUM', 10, 12, 120),
-- (1, 'Sala 3D 1', 'MEDIUM', 10, 12, 120), (1, 'Sala 3D 2', 'MEDIUM', 10, 12, 120), (1, 'Sala 3D 3', 'SMALL', 8, 10, 80),
-- (1, 'Sala 2D 1', 'LARGE', 12, 14, 168), (1, 'Sala 2D 2', 'LARGE', 12, 14, 168), (1, 'Sala 2D 3', 'MEDIUM', 10, 12, 120),
-- (1, 'Sala 2D 4', 'MEDIUM', 10, 12, 120), (1, 'Sala 2D 5', 'SMALL', 8, 10, 80), (1, 'Sala 2D 6', 'SMALL', 8, 10, 80),
-- Cinema 2: Angamos (theater_id 13-24)
-- (2, 'Sala XD 1', 'LARGE', 12, 15, 180), (2, 'Sala XD 2', 'LARGE', 12, 15, 180), (2, 'Sala XD 3', 'MEDIUM', 10, 12, 120),
-- (2, 'Sala 3D 1', 'MEDIUM', 10, 12, 120), (2, 'Sala 3D 2', 'MEDIUM', 10, 12, 120), (2, 'Sala 3D 3', 'SMALL', 8, 10, 80),
-- (2, 'Sala 2D 1', 'LARGE', 12, 14, 168), (2, 'Sala 2D 2', 'LARGE', 12, 14, 168), (2, 'Sala 2D 3', 'MEDIUM', 10, 12, 120),
-- (2, 'Sala 2D 4', 'MEDIUM', 10, 12, 120), (2, 'Sala 2D 5', 'SMALL', 8, 10, 80), (2, 'Sala 2D 6', 'SMALL', 8, 10, 80),
-- Cinema 3: Arequipa (theater_id 25-36)
-- (3, 'Sala XD 1', 'LARGE', 12, 15, 180), (3, 'Sala XD 2', 'LARGE', 12, 15, 180), (3, 'Sala XD 3', 'MEDIUM', 10, 12, 120),
-- (3, 'Sala 3D 1', 'MEDIUM', 10, 12, 120), (3, 'Sala 3D 2', 'MEDIUM', 10, 12, 120), (3, 'Sala 3D 3', 'SMALL', 8, 10, 80),
-- (3, 'Sala 2D 1', 'LARGE', 12, 14, 168), (3, 'Sala 2D 2', 'LARGE', 12, 14, 168), (3, 'Sala 2D 3', 'MEDIUM', 10, 12, 120),
-- (3, 'Sala 2D 4', 'MEDIUM', 10, 12, 120), (3, 'Sala 2D 5', 'SMALL', 8, 10, 80), (3, 'Sala 2D 6', 'SMALL', 8, 10, 80),
-- Cinema 4: Asia (theater_id 37-48)
-- (4, 'Sala XD 1', 'LARGE', 12, 15, 180), (4, 'Sala XD 2', 'LARGE', 12, 15, 180), (4, 'Sala XD 3', 'MEDIUM', 10, 12, 120),
-- (4, 'Sala 3D 1', 'MEDIUM', 10, 12, 120), (4, 'Sala 3D 2', 'MEDIUM', 10, 12, 120), (4, 'Sala 3D 3', 'SMALL', 8, 10, 80),
-- (4, 'Sala 2D 1', 'LARGE', 12, 14, 168), (4, 'Sala 2D 2', 'LARGE', 12, 14, 168), (4, 'Sala 2D 3', 'MEDIUM', 10, 12, 120),
-- (4, 'Sala 2D 4', 'MEDIUM', 10, 12, 120), (4, 'Sala 2D 5', 'SMALL', 8, 10, 80), (4, 'Sala 2D 6', 'SMALL', 8, 10, 80),
-- Cinema 5: Bellavista (theater_id 49-60)
-- (5, 'Sala XD 1', 'LARGE', 12, 15, 180), (5, 'Sala XD 2', 'LARGE', 12, 15, 180), (5, 'Sala XD 3', 'MEDIUM', 10, 12, 120),
-- (5, 'Sala 3D 1', 'MEDIUM', 10, 12, 120), (5, 'Sala 3D 2', 'MEDIUM', 10, 12, 120), (5, 'Sala 3D 3', 'SMALL', 8, 10, 80),
-- (5, 'Sala 2D 1', 'LARGE', 12, 14, 168), (5, 'Sala 2D 2', 'LARGE', 12, 14, 168), (5, 'Sala 2D 3', 'MEDIUM', 10, 12, 120),
-- (5, 'Sala 2D 4', 'MEDIUM', 10, 12, 120), (5, 'Sala 2D 5', 'SMALL', 8, 10, 80), (5, 'Sala 2D 6', 'SMALL', 8, 10, 80),
-- Cinema 6: Gamarra (theater_id 61-72)
-- (6, 'Sala XD 1', 'LARGE', 12, 15, 180), (6, 'Sala XD 2', 'LARGE', 12, 15, 180), (6, 'Sala XD 3', 'MEDIUM', 10, 12, 120),
-- (6, 'Sala 3D 1', 'MEDIUM', 10, 12, 120), (6, 'Sala 3D 2', 'MEDIUM', 10, 12, 120), (6, 'Sala 3D 3', 'SMALL', 8, 10, 80),
-- (6, 'Sala 2D 1', 'LARGE', 12, 14, 168), (6, 'Sala 2D 2', 'LARGE', 12, 14, 168), (6, 'Sala 2D 3', 'MEDIUM', 10, 12, 120),
-- (6, 'Sala 2D 4', 'MEDIUM', 10, 12, 120), (6, 'Sala 2D 5', 'SMALL', 8, 10, 80), (6, 'Sala 2D 6', 'SMALL', 8, 10, 80),
-- Cinema 7: Jockey Plaza (theater_id 73-84)
-- (7, 'Sala XD 1', 'LARGE', 12, 15, 180), (7, 'Sala XD 2', 'LARGE', 12, 15, 180), (7, 'Sala XD 3', 'MEDIUM', 10, 12, 120),
-- (7, 'Sala 3D 1', 'MEDIUM', 10, 12, 120), (7, 'Sala 3D 2', 'MEDIUM', 10, 12, 120), (7, 'Sala 3D 3', 'SMALL', 8, 10, 80),
-- (7, 'Sala 2D 1', 'LARGE', 12, 14, 168), (7, 'Sala 2D 2', 'LARGE', 12, 14, 168), (7, 'Sala 2D 3', 'MEDIUM', 10, 12, 120),
-- (7, 'Sala 2D 4', 'MEDIUM', 10, 12, 120), (7, 'Sala 2D 5', 'SMALL', 8, 10, 80), (7, 'Sala 2D 6', 'SMALL', 8, 10, 80),
-- Cinema 8: Lambra (theater_id 85-96)
-- (8, 'Sala XD 1', 'LARGE', 12, 15, 180), (8, 'Sala XD 2', 'LARGE', 12, 15, 180), (8, 'Sala XD 3', 'MEDIUM', 10, 12, 120),
-- (8, 'Sala 3D 1', 'MEDIUM', 10, 12, 120), (8, 'Sala 3D 2', 'MEDIUM', 10, 12, 120), (8, 'Sala 3D 3', 'SMALL', 8, 10, 80),
-- (8, 'Sala 2D 1', 'LARGE', 12, 14, 168), (8, 'Sala 2D 2', 'LARGE', 12, 14, 168), (8, 'Sala 2D 3', 'MEDIUM', 10, 12, 120),
-- (8, 'Sala 2D 4', 'MEDIUM', 10, 12, 120), (8, 'Sala 2D 5', 'SMALL', 8, 10, 80), (8, 'Sala 2D 6', 'SMALL', 8, 10, 80);
