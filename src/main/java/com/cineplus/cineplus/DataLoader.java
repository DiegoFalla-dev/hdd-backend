package com.cineplus.cineplus;

import com.cineplus.cineplus.domain.entity.*;
import com.cineplus.cineplus.domain.entity.Role.RoleName;
import com.cineplus.cineplus.domain.repository.RoleRepository;
import com.cineplus.cineplus.domain.repository.ShowtimeRepository;
import com.cineplus.cineplus.domain.repository.TicketTypeRepository;
import com.cineplus.cineplus.domain.repository.CinemaRepository;
import com.cineplus.cineplus.domain.repository.ConcessionProductRepository;
import com.cineplus.cineplus.domain.entity.MovieStatus;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final ShowtimeRepository showtimeRepository;
    private final TicketTypeRepository ticketTypeRepository;
    private final CinemaRepository cinemaRepository;
    private final ConcessionProductRepository concessionProductRepository;
    private final com.cineplus.cineplus.domain.repository.MovieRepository movieRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Asegúrate de que los roles existan (Lógica correcta, sin cambios)
        if (roleRepository.findByName(RoleName.ROLE_ADMIN).isEmpty()) {
            roleRepository.save(new Role(null, RoleName.ROLE_ADMIN));
        }
        if (roleRepository.findByName(RoleName.ROLE_MANAGER).isEmpty()) {
            roleRepository.save(new Role(null, RoleName.ROLE_MANAGER));
        }
        if (roleRepository.findByName(RoleName.ROLE_USER).isEmpty()) {
            roleRepository.save(new Role(null, RoleName.ROLE_USER));
        }

        // Seed default ticket types if none exist (Lógica correcta, sin cambios)
        try {
            if (ticketTypeRepository.count() == 0) {
                ticketTypeRepository.saveAll(java.util.List.of(
                        TicketType.builder().code("PROMO_ONLINE").name("PROMO ONLINE").price(BigDecimal.valueOf(14.96)).active(true).build(),
                        TicketType.builder().code("PERSONA_CON_DISCAPACIDAD").name("PERSONA CON DISCAPACIDAD").price(BigDecimal.valueOf(17.70)).active(true).build(),
                        TicketType.builder().code("SILLA_DE_RUEDAS").name("SILLA DE RUEDAS").price(BigDecimal.valueOf(17.70)).active(true).build(),
                        TicketType.builder().code("NINO").name("NIÑO").price(BigDecimal.valueOf(21.60)).active(true).build(),
                        TicketType.builder().code("ADULTO").name("ADULTO").price(BigDecimal.valueOf(23.60)).active(true).build()
                ));
            }
        } catch (Exception ex) {
            System.err.println("DataLoader: error seeding ticket types: " + ex.getMessage());
        }

        // --- INICIO DE CAMBIOS ---

        // Recomendación: Cargar Cinemas si no existen (manteniendo el chequeo de count para evitar recrear salas)
        if (cinemaRepository.count() == 0) {
            loadCinemas();
        }

        // Recomendación: Llama a los métodos de carga de productos y películas directamente
        // Ellos tienen la lógica de "si ya existe, no lo guardes"
        loadConcessionProducts();
        loadMovies();

        // --- FIN DE CAMBIOS ---
    }

    private void loadCinemas() {
        Set<Cinema> cinemas = new HashSet<>();

        Cinema cinema1 = new Cinema();
        cinema1.setName("Cineplus Real Plaza Trujillo");
        cinema1.setCity("Trujillo");
        cinema1.setAddress("Av. América Sur 1111, Trujillo");
        cinema1.setLocation("Real Plaza Trujillo");
        cinema1.setAvailableFormats(Arrays.asList("2D"));
        cinema1.setImage("https://i.imgur.com/EyXfJf3.png");
        cinemas.add(cinema1);

        Cinema cinema2 = new Cinema();
        cinema2.setName("Cineplus Angamos");
        cinema2.setCity("Lima");
        cinema2.setAddress("Av. Angamos Oeste 1803, Miraflores");
        cinema2.setLocation("Angamos");
        cinema2.setAvailableFormats(Arrays.asList("2D", "3D"));
        cinema2.setImage("https://i.imgur.com/YSIYTYI.png");
        cinemas.add(cinema2);

        Cinema cinema3 = new Cinema();
        cinema3.setName("Cineplus Arequipa");
        cinema3.setCity("Arequipa");
        cinema3.setAddress("Av. Ejército 793, Cayma");
        cinema3.setLocation("Arequipa");
        cinema3.setAvailableFormats(Arrays.asList("2D", "3D"));
        cinema3.setImage("https://i.imgur.com/M6qIkFc.png");
        cinemas.add(cinema3);

        Cinema cinema4 = new Cinema();
        cinema4.setName("Cineplus Asia");
        cinema4.setCity("Lima");
        cinema4.setAddress("Km 97.5 Panamericana Sur, Asia");
        cinema4.setLocation("Asia");
        cinema4.setAvailableFormats(Arrays.asList("2D", "3D"));
        cinema4.setImage("https://i.imgur.com/OhzUP03.png");
        cinemas.add(cinema4);

        Cinema cinema5 = new Cinema();
        cinema5.setName("Cineplus Bellavista");
        cinema5.setCity("Callao");
        cinema5.setAddress("Av. Colonial 4000, Bellavista");
        cinema5.setLocation("Bellavista");
        cinema5.setAvailableFormats(Arrays.asList("2D", "3D"));
        cinema5.setImage("https://i.imgur.com/ewcJ4fD.png");
        cinemas.add(cinema5);

        Cinema cinema6 = new Cinema();
        cinema6.setName("Cineplus Gamarra");
        cinema6.setCity("Lima");
        cinema6.setAddress("Jr. Gamarra 1000, La Victoria");
        cinema6.setLocation("Gamarra");
        cinema6.setAvailableFormats(Arrays.asList("2D", "3D"));
        cinema6.setImage("https://i.imgur.com/nynFqFZ.png");
        cinemas.add(cinema6);

        Cinema cinema7 = new Cinema();
        cinema7.setName("Cineplus Jockey Plaza");
        cinema7.setCity("Lima");
        cinema7.setAddress("Av. Javier Prado Este 4200, Santiago de Surco");
        cinema7.setLocation("Jockey Plaza");
        cinema7.setAvailableFormats(Arrays.asList("2D", "3D"));
        cinema7.setImage("https://i.imgur.com/5pVThyZ.png");
        cinemas.add(cinema7);

        Cinema cinema8 = new Cinema();
        cinema8.setName("Cineplus Lambra");
        cinema8.setCity("Lima");
        cinema8.setAddress("Av. La Molina 1100, La Molina");
        cinema8.setLocation("Lambra");
        cinema8.setAvailableFormats(Arrays.asList("2D", "3D"));
        cinema8.setImage("https://i.imgur.com/LD1nrhu.png");
        cinemas.add(cinema8);

        cinemaRepository.saveAll(cinemas);
    }

    // --- MÉTODO OPTIMIZADO PARA PRODUCTOS ---
    private void loadConcessionProducts() {
        // Get all cinemas
        java.util.List<Cinema> allCinemas = cinemaRepository.findAll();
        Set<Cinema> cinemaSet = new HashSet<>(allCinemas);

        // Define all products
        java.util.List<ConcessionProduct> productsToLoad = java.util.Arrays.asList(
                // COMBOS
                buildProduct("Combo Caliente", "Canchita grande + Gaseosa grande + Hot dog", BigDecimal.valueOf(50.20), "https://i.imgur.com/3QXTlTP.png", ConcessionProduct.ProductCategory.COMBOS, cinemaSet),
                buildProduct("Combo Sal", "Canchita mediana + Gaseosa grande + Nachos", BigDecimal.valueOf(48.30), "https://i.imgur.com/mn8MkFr.png", ConcessionProduct.ProductCategory.COMBOS, cinemaSet),
                buildProduct("Combo Familiar", "4 Canchitas grandes + 3 Gaseosas grandes", BigDecimal.valueOf(149.00), "https://i.imgur.com/8mqvkF4.png", ConcessionProduct.ProductCategory.COMBOS, cinemaSet),
                buildProduct("Combo Dulce", "Canchita grande dulce + Frugos + Nuggets x6", BigDecimal.valueOf(47.40), "https://i.imgur.com/E9ZykEX.png", ConcessionProduct.ProductCategory.COMBOS, cinemaSet),
                buildProduct("Combo Pareja", "2 Canchitas medianas + 2 Gaseosas grandes", BigDecimal.valueOf(69.90), "https://i.imgur.com/dkPseQ3.png", ConcessionProduct.ProductCategory.COMBOS, cinemaSet),
                buildProduct("Combo Burger Premium", "Canchita gigante + 2 Gaseosas grandes + Hamburguesa", BigDecimal.valueOf(35.90), "https://i.imgur.com/4f5OE64.png", ConcessionProduct.ProductCategory.COMBOS, cinemaSet),
                // CANCHITA
                buildProduct("Canchita Gigante Salada", "Porción gigante de canchita con sal", BigDecimal.valueOf(34.90), "https://i.imgur.com/6X0TjP9.png", ConcessionProduct.ProductCategory.CANCHITA, cinemaSet),
                buildProduct("Canchita Grande Salada", "Porción grande de canchita con sal", BigDecimal.valueOf(29.90), "https://i.imgur.com/yoalqQt.png", ConcessionProduct.ProductCategory.CANCHITA, cinemaSet),
                buildProduct("Canchita Grande Dulce", "Porción grande de canchita dulce", BigDecimal.valueOf(31.90), "https://i.imgur.com/4BCV5nd.png", ConcessionProduct.ProductCategory.CANCHITA, cinemaSet),
                buildProduct("Canchita Mediana Salada", "Porción mediana de canchita con sal", BigDecimal.valueOf(26.90), "https://i.imgur.com/uJlVCc9.png", ConcessionProduct.ProductCategory.CANCHITA, cinemaSet),
                buildProduct("Canchita Mediana Dulce", "Porción mediana de canchita dulce", BigDecimal.valueOf(27.90), "https://i.imgur.com/UCS4p8y.png", ConcessionProduct.ProductCategory.CANCHITA, cinemaSet),
                buildProduct("Canchita Kids Salada", "Porción Kids de canchita con sal", BigDecimal.valueOf(11.90), "https://i.imgur.com/JBRSxds.png", ConcessionProduct.ProductCategory.CANCHITA, cinemaSet),
                buildProduct("Canchita Kids Dulce", "Canchita Kids dulce", BigDecimal.valueOf(12.90), "https://i.imgur.com/Nu0Ow6U.png", ConcessionProduct.ProductCategory.CANCHITA, cinemaSet),
                buildProduct("Canchita Mantequilla", "Canchita grande con mantequilla", BigDecimal.valueOf(31.90), "https://i.imgur.com/oSnb1b2.png", ConcessionProduct.ProductCategory.CANCHITA, cinemaSet),
                // BEBIDAS
                buildProduct("Coca Cola Grande", "Coca Cola 500ml", BigDecimal.valueOf(11.90), "https://i.imgur.com/H0od7eK.png", ConcessionProduct.ProductCategory.BEBIDAS, cinemaSet),
                buildProduct("Inca Kola Grande", "Inca Kola 500ml", BigDecimal.valueOf(11.90), "https://i.imgur.com/nUkZc3u.png", ConcessionProduct.ProductCategory.BEBIDAS, cinemaSet),
                buildProduct("Sprite Grande", "Sprite 500ml", BigDecimal.valueOf(11.90), "https://i.imgur.com/YrRj40A.png", ConcessionProduct.ProductCategory.BEBIDAS, cinemaSet),
                buildProduct("Jugo de Naranja", "Jugo natural de naranja 400ml", BigDecimal.valueOf(11.90), "https://i.imgur.com/1gzH5cZ.png", ConcessionProduct.ProductCategory.BEBIDAS, cinemaSet),
                buildProduct("Frugos del valle", "Frugos 300ml", BigDecimal.valueOf(6.90), "https://i.imgur.com/wPEQYeE.png", ConcessionProduct.ProductCategory.BEBIDAS, cinemaSet),
                buildProduct("Agua con Gas", "Agua mineral con gas 500ml", BigDecimal.valueOf(6.90), "https://i.imgur.com/2S1tlC6.png", ConcessionProduct.ProductCategory.BEBIDAS, cinemaSet),
                buildProduct("Agua Sin Gas", "Agua mineral 500ml", BigDecimal.valueOf(5.90), "https://i.imgur.com/kAjLTU3.png", ConcessionProduct.ProductCategory.BEBIDAS, cinemaSet),
                // SNACKS
                buildProduct("Hot Dog Frankfurter", "Hot dog con salchicha alemana y salsas", BigDecimal.valueOf(13.90), "https://i.imgur.com/vgYqN6n.png", ConcessionProduct.ProductCategory.SNACKS, cinemaSet),
                buildProduct("Nachos con Queso", "Nachos crujientes con salsa de queso", BigDecimal.valueOf(14.90), "https://i.imgur.com/fmBuiPG.png", ConcessionProduct.ProductCategory.SNACKS, cinemaSet),
                buildProduct("Papas Fritas", "Papas fritas crujientes porción grande", BigDecimal.valueOf(7.90), "https://i.imgur.com/X5f8YC9.png", ConcessionProduct.ProductCategory.SNACKS, cinemaSet),
                buildProduct("Tequeños x4 un", "4 tequeños de queso fritos", BigDecimal.valueOf(10.90), "https://i.imgur.com/bDMNPBk.png", ConcessionProduct.ProductCategory.SNACKS, cinemaSet),
                buildProduct("Nuggets x6", "6 nuggets de pollo crujientes", BigDecimal.valueOf(13.90), "https://i.imgur.com/MtnELKD.png", ConcessionProduct.ProductCategory.SNACKS, cinemaSet),
                buildProduct("Salchipapas", "Papas fritas con salchicha y salsas", BigDecimal.valueOf(14.90), "https://i.imgur.com/aF0NUuv.png", ConcessionProduct.ProductCategory.SNACKS, cinemaSet),
                buildProduct("Hamburguesa Clásica", "Hamburguesa con carne, lechuga y tomate", BigDecimal.valueOf(16.90), "https://i.imgur.com/8U3R1Oa.png", ConcessionProduct.ProductCategory.SNACKS, cinemaSet),
                buildProduct("Pizza Personal", "Pizza individual de pepperoni", BigDecimal.valueOf(18.90), "https://i.imgur.com/oMEqVGb.png", ConcessionProduct.ProductCategory.SNACKS, cinemaSet),
                buildProduct("Sandwich Club", "Sandwich triple con pollo, tocino y verduras", BigDecimal.valueOf(16.90), "https://i.imgur.com/jwVxk1i.png", ConcessionProduct.ProductCategory.SNACKS, cinemaSet),
                buildProduct("Alitas BBQ x7", "7 alitas de pollo con salsa BBQ", BigDecimal.valueOf(18.90), "https://i.imgur.com/cXOORTE.png", ConcessionProduct.ProductCategory.SNACKS, cinemaSet),
                buildProduct("Quesadilla", "Quesadilla de queso con guacamole", BigDecimal.valueOf(15.90), "https://i.imgur.com/oK4ZOoP.png", ConcessionProduct.ProductCategory.SNACKS, cinemaSet),
                buildProduct("Wrap de Pollo", "Wrap con pollo, lechuga y salsa ranch", BigDecimal.valueOf(19.90), "https://i.imgur.com/VwpHY4O.png", ConcessionProduct.ProductCategory.SNACKS, cinemaSet)
        );

        // 1. Obtener todos los nombres de productos existentes de forma eficiente
        Set<String> existingProductNames = concessionProductRepository.findAll().stream()
                .map(ConcessionProduct::getName)
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        // 2. Filtrar los productos que no existen
        List<ConcessionProduct> productsToSave = productsToLoad.stream()
                .filter(p -> !existingProductNames.contains(p.getName().toLowerCase()))
                .peek(p -> p.setAvailable(true)) // Asegurar que estén disponibles antes de guardar
                .collect(Collectors.toList());

        // 3. Guardar el lote de productos nuevos
        if (!productsToSave.isEmpty()) {
            concessionProductRepository.saveAll(productsToSave);
        }
    }

    private ConcessionProduct buildProduct(String name, String description, BigDecimal price, String imageUrl, ConcessionProduct.ProductCategory category, Set<Cinema> cinemas) {
        ConcessionProduct p = new ConcessionProduct();
        p.setName(name);
        p.setDescription(description);
        p.setPrice(price);
        p.setImageUrl(imageUrl);
        p.setCategory(category);
        p.setCinemas(cinemas);
        p.setAvailable(true);
        return p;
    }

    // --- MÉTODO OPTIMIZADO PARA PELÍCULAS (Tu versión anterior, ahora en su lugar) ---
    private void loadMovies() {
        // 1. Lista de películas únicas a cargar
        List<Movie> moviesToLoad = List.of(
                new Movie(null, "Concierto de Navidad de André Rieu 2025", "Esta temporada navideña, la magia comienza en la gran pantalla. Únete a André Rieu en su concierto de Navidad 2025.", "Concierto", "R", "2hrs 35min", "https://cdn.apis.cineplanet.com.pe/CDN/media/entity/get/FilmPosterGraphic/HO00002676?referenceScheme=HeadOffice&allowPlaceHolder=true", null, null, null, null, MovieStatus.CARTELERA),
                new Movie(null, "Beso de tres", "Connor, un joven amable pero inseguro, lleva años enamorado de su amiga Olivia y queda atrapado en un trío inesperado.", "Drama", "+14", "1hrs 52min", "https://cdn.apis.cineplanet.com.pe/CDN/media/entity/get/FilmPosterGraphic/HO00002628?referenceScheme=HeadOffice&allowPlaceHolder=true", null, "https://youtu.be/bBDpvM6sPEY", null, null, MovieStatus.CARTELERA),

                new Movie(null, "El lado oscuro de la justicia", "Un joven pobre es acusado de tráfico de drogas. Un exfiscal investiga el caso y descubre un plan de abogados corruptos.", "Acción", "+14", "2hrs 0min", "https://cdn.apis.cineplanet.com.pe/CDN/media/entity/get/FilmPosterGraphic/HO00002697?referenceScheme=HeadOffice&allowPlaceHolder=true", "https://cdnpe.cineplanet.com.pe/assets/1072558e-e442-4c8c-95c9-60bf1c13bfa8", "https://www.youtube.com/watch?v=ycXCAIUZLHw", null, null, MovieStatus.CARTELERA),
                new Movie(null, "Fue Solo Un Accidente", "Lo que comenzó como un pequeño accidente pone en movimiento una serie de consecuencias cada vez mayores.", "Drama", "+14", "1hrs 45min", "https://cdn.apis.cineplanet.com.pe/CDN/media/entity/get/FilmPosterGraphic/HO00002698?referenceScheme=HeadOffice&allowPlaceHolder=true", "https://cdnpe.cineplanet.com.pe/assets/85825100-c329-49bf-bd93-0b9002406fda", "https://www.youtube.com/watch?v=BXJbgWNcDxE", null, null, MovieStatus.CARTELERA),
                new Movie(null, "Five Nights at Freddy's 2", "Ha pasado un año desde la pesadilla sobrenatural en Freddy Fazbear's Pizza. La familia vuelve a enfrentar el terror.", "Terror", "+14", "1hrs 45min", "https://cdn.apis.cineplanet.com.pe/CDN/media/entity/get/FilmPosterGraphic/HO00002582?referenceScheme=HeadOffice&allowPlaceHolder=true", "https://cdnpe.cineplanet.com.pe/assets/80f68fae-db70-45cb-87bd-5392a78dded2", "https://www.youtube.com/watch?v=E8M-iJ0p-Xk", null, null, MovieStatus.CARTELERA),
                new Movie(null, "Kiki Entregas a Domicilio", "Kiki es una joven bruja que al cumplir 13 años debe encontrar su lugar en el mundo con su servicio de entregas voladoras.", "Anime", "APT", "1hrs 52min", "https://cdn.apis.cineplanet.com.pe/CDN/media/entity/get/FilmPosterGraphic/HO00002503?referenceScheme=HeadOffice&allowPlaceHolder=true", null, "https://www.youtube.com/watch?v=lHhMseKJFWY", null, null, MovieStatus.CARTELERA),
                new Movie(null, "Sirenas", "En La Casa de las Sirenas, el burdel más antiguo de Iquitos, el carnaval se torna en misterio al hallarse muerta a la menor.", "Thriller", "+14", "1hrs 45min", "https://cdn.apis.cineplanet.com.pe/CDN/media/entity/get/FilmPosterGraphic/HO00002689?referenceScheme=HeadOffice&allowPlaceHolder=true", "https://cdnpe.cineplanet.com.pe/assets/e56b2a1f-03d9-4bcf-98bf-10d54ee88b17", "https://youtu.be/etE2HYY7tRI", null, null, MovieStatus.CARTELERA),
                new Movie(null, "Una Navidad Inesperada", "Una pareja lleva a su hija al hotel de su abuelo para revelarle su separación, pero la niña intentará reunir a la familia.", "Comedia", "+14", "1hrs 32min", "https://cdn.apis.cineplanet.com.pe/CDN/media/entity/get/FilmPosterGraphic/HO00002690?referenceScheme=HeadOffice&allowPlaceHolder=true", "https://cdnpe.cineplanet.com.pe/assets/7c4fbc84-f4bd-4697-9d8b-7362703054bc", "https://www.youtube.com/watch?v=mHDz_kgQOuI", null, null, MovieStatus.CARTELERA),
                new Movie(null, "MONSTA X: CONNECT X IN CINEMAS", "MONSTA X incendió el KSPO DOME durante tres noches inolvidables. Una crónica de diez años juntos.", "Concierto", "+14", "1hrs 58min", "https://cdn.apis.cineplanet.com.pe/CDN/media/entity/get/FilmPosterGraphic/HO00002675?referenceScheme=HeadOffice&allowPlaceHolder=true", null, "https://www.youtube.com/watch?v=P11rZPHKxb4", null, null, MovieStatus.CARTELERA),
                new Movie(null, "Zootopia 2", "Los detectives Judy Hopps y Nick Wilde se enfrentan a su misión más salvaje: un misterioso reptil llegó a la ciudad.", "Animación", "APT", "1hrs 48min", "https://cdn.apis.cineplanet.com.pe/CDN/media/entity/get/FilmPosterGraphic/HO00002477?referenceScheme=HeadOffice&allowPlaceHolder=true", "https://i.pinimg.com/1200x/d4/9c/cd/d49ccd324765da7c7b4a39f0105f99a7.jpg", "https://www.youtube.com/watch?v=4_KaABhCPPk", null, null, MovieStatus.CARTELERA),

                // PREVENTA
                new Movie(null, "Crítica Abierta: Bugonia", "Función especial con conversatorio. Dos jóvenes secuestran a una CEO convencidos de que es una alienígena.", "Drama", "+14", "2hrs 0min", "https://cdn.apis.cineplanet.com.pe/CDN/media/entity/get/FilmPosterGraphic/HO00002719?referenceScheme=HeadOffice&allowPlaceHolder=true", null, "https://www.youtube.com/watch?v=jVp8DR0ObDU", null, null, MovieStatus.PREVENTA),
                new Movie(null, "Amanecer Parte 1", "Edward y Bella se casan. Durante la luna de miel, Bella queda embarazada y su salud se deteriora rápidamente.", "Romance", "+14", "1hrs 57min", "https://cdn.apis.cineplanet.com.pe/CDN/media/entity/get/FilmPosterGraphic/HO00002653?referenceScheme=HeadOffice&allowPlaceHolder=true", null, null, null, null, MovieStatus.PREVENTA),
                new Movie(null, "Amanecer Parte 2", "Bella se adapta a su nueva naturaleza vampira. La familia Cullen debe protegerse de la amenaza de los Volturi.", "Romance", "+14", "1hrs 58min", "https://cdn.apis.cineplanet.com.pe/CDN/media/entity/get/FilmPosterGraphic/HO00002654?referenceScheme=HeadOffice&allowPlaceHolder=true", null, "https://www.youtube.com/watch?v=zJL0l-7MuWw", null, null, MovieStatus.PREVENTA),
                new Movie(null, "El Grinch", "En las afueras de Whoville, vive el Grinch y busca venganza para arruinar la Navidad de todos.", "Familiar", "APT", "1hrs 44min", "https://cdn.apis.cineplanet.com.pe/CDN/media/entity/get/FilmPosterGraphic/HO00002701?referenceScheme=HeadOffice&allowPlaceHolder=true", null, null, null, null, MovieStatus.PREVENTA),
                new Movie(null, "Avatar Fuego y Cenizas", "La familia de Jake y Neytiri se enfrenta a una tribu Na'vi hostil, los Ash, mientras los conflictos en Pandora se intensifican.", "Acción", "+14", "3hrs 14min", "https://cdn.apis.cineplanet.com.pe/CDN/media/entity/get/FilmPosterGraphic/HO00002638?referenceScheme=HeadOffice&allowPlaceHolder=true", null, "https://www.youtube.com/watch?v=g71Ha1HCWt8", null, null, MovieStatus.PREVENTA),

                // PROXIMO
                new Movie(null, "100 Metros", "Togashi, estrella del atletismo, conoce al estudiante Komiya. Pasarán los años y sus destinos se cruzarán como rivales.", "Anime", "+14", "1hrs 46min", "https://cdn.apis.cineplanet.com.pe/CDN/media/entity/get/FilmPosterGraphic/HO00002700?referenceScheme=HeadOffice&allowPlaceHolder=true", null, null, null, null, MovieStatus.PROXIMO),
                new Movie(null, "Bugonia", "Dos jóvenes obsesionados con conspiraciones secuestran a una CEO convencidos de que es una alienígena.", "Drama", "+14", "2hrs 0min", "https://cdn.apis.cineplanet.com.pe/CDN/media/entity/get/FilmPosterGraphic/HO00002666?referenceScheme=HeadOffice&allowPlaceHolder=true", null, null, null, null, MovieStatus.PROXIMO),
                new Movie(null, "El Descubridor de Leyendas", "El profesor Fang lidera una expedición al Templo del Glaciar en busca de artefactos que conectan sueños y realidad.", "Acción", "+14", "2hrs 0min", "https://cdn.apis.cineplanet.com.pe/CDN/media/entity/get/FilmPosterGraphic/HO00002718?referenceScheme=HeadOffice&allowPlaceHolder=true", null, "https://www.youtube.com/watch?v=cEFCr0B6udc", null, null, MovieStatus.PROXIMO),
                new Movie(null, "El gran premio a toda velocidad", "Edda, una joven ratoncita, tiene la oportunidad de competir en el Gran Premio disfrazada como su ídolo.", "Animación", "APT", "2hrs 0min", "https://cdn.apis.cineplanet.com.pe/CDN/media/entity/get/FilmPosterGraphic/HO00002478?referenceScheme=HeadOffice&allowPlaceHolder=true", null, "https://www.youtube.com/watch?v=cEFCr0B6udc", null, null, MovieStatus.PROXIMO),
                new Movie(null, "Fuimos Héroes", "En 1975, la selección peruana conquistó su segunda Copa América. Esta es la historia de esos héroes.", "Drama", "+14", "55min", "https://cdn.apis.cineplanet.com.pe/CDN/media/entity/get/FilmPosterGraphic/HO00002685?referenceScheme=HeadOffice&allowPlaceHolder=true", null, null, null, null, MovieStatus.PROXIMO),
                new Movie(null, "Noche de Paz, Noche de Horror", "Un niño presencia el asesinato de sus padres. Años después, se disfraza de Papá Noel y busca venganza.", "Terror", "+14", "1hrs 36min", "https://cdn.apis.cineplanet.com.pe/CDN/media/entity/get/FilmPosterGraphic/HO00002662?referenceScheme=HeadOffice&allowPlaceHolder=true", null, null, null, null, MovieStatus.PROXIMO)
        );

        // 2. Optimización: Se obtienen TODOS los títulos existentes una sola vez.
        Set<String> existingTitles = movieRepository.findAll().stream()
                .map(Movie::getTitle)
                .map(String::toLowerCase) // Para comparación sin distinción entre mayúsculas y minúsculas
                .collect(Collectors.toSet());

        // 3. Se filtra la lista para obtener solo las películas que no existen
        List<Movie> moviesToSave = moviesToLoad.stream()
                .filter(m -> !existingTitles.contains(m.getTitle().toLowerCase()))
                .collect(Collectors.toList());

        // 4. Se guarda el nuevo lote de películas de forma eficiente (batch insert)
        if (!moviesToSave.isEmpty()) {
            movieRepository.saveAll(moviesToSave);
        }
    }
}