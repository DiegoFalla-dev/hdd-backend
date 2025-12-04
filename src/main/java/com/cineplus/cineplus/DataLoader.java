package com.cineplus.cineplus;

import com.cineplus.cineplus.domain.entity.Role;
import com.cineplus.cineplus.domain.entity.Role.RoleName;
import com.cineplus.cineplus.domain.repository.RoleRepository;
import com.cineplus.cineplus.domain.repository.ShowtimeRepository;
import com.cineplus.cineplus.domain.repository.TicketTypeRepository;
import com.cineplus.cineplus.domain.repository.CinemaRepository;
import com.cineplus.cineplus.domain.repository.ConcessionProductRepository;
import com.cineplus.cineplus.domain.entity.TicketType;
import com.cineplus.cineplus.domain.entity.Cinema;
import com.cineplus.cineplus.domain.entity.ConcessionProduct;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
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

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Asegúrate de que los roles existan
        if (roleRepository.findByName(RoleName.ROLE_ADMIN).isEmpty()) {
            roleRepository.save(new Role(null, RoleName.ROLE_ADMIN));
        }
        if (roleRepository.findByName(RoleName.ROLE_MANAGER).isEmpty()) {
            roleRepository.save(new Role(null, RoleName.ROLE_MANAGER));
        }
        if (roleRepository.findByName(RoleName.ROLE_USER).isEmpty()) {
            roleRepository.save(new Role(null, RoleName.ROLE_USER));
        }



        // Seed default ticket types if none exist
        try {
            if (ticketTypeRepository.count() == 0) {
                ticketTypeRepository.saveAll(java.util.List.of(
                        TicketType.builder().code("PROMO_ONLINE").name("PROMO ONLINE").price(BigDecimal.valueOf(14.96)).active(true).build(),
                        TicketType.builder().code("PERSONA_CON_DISCAPACIDAD").name("PERSONA CON DISCAPACIDAD").price(BigDecimal.valueOf(17.70)).active(true).build(),
                        TicketType.builder().code("SILLA_DE_RUEDAS").name("SILLA DE RUEDAS").price(BigDecimal.valueOf(17.70)).active(true).build(),
                        TicketType.builder().code("NINO").name("NIÑO").price(BigDecimal.valueOf(21.60)).active(true).build(),
                        TicketType.builder().code("ADULTO").name("ADULTO").price(BigDecimal.valueOf(23.60)).active(true).build(),
                        TicketType.builder().code("50_DCTO_BANCO_RIPLEY").name("50% DCTO BANCO RIPLEY").price(BigDecimal.valueOf(12.80)).active(true).build()
                ));
            }
        } catch (Exception ex) {
            System.err.println("DataLoader: error seeding ticket types: " + ex.getMessage());
        }

        // Load Cinemas if they don't exist
        if (cinemaRepository.count() == 0) {
            loadCinemas();
        }

        // Load Concession Products if they don't exist
        if (concessionProductRepository.count() == 0) {
            loadConcessionProducts();
        }
    }

    private void loadCinemas() {
        Set<Cinema> cinemas = new HashSet<>();

        Cinema cinema1 = new Cinema();
        cinema1.setName("Cineplus Real Plaza Trujillo");
        cinema1.setCity("Trujillo");
        cinema1.setAddress("Av. América Sur 1111, Trujillo");
        cinema1.setLocation("Real Plaza Trujillo");
        cinema1.setAvailableFormats(Arrays.asList("2D"));
        cinema1.setImage(null);
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

    private void loadConcessionProducts() {
        // Get all cinemas
        java.util.List<Cinema> allCinemas = cinemaRepository.findAll();
        Set<Cinema> cinemaSet = new HashSet<>(allCinemas);

        // COMBOS
        ConcessionProduct combo1 = new ConcessionProduct();
        combo1.setName("Combo Caliente");
        combo1.setDescription("Canchita grande + Gaseosa grande + Hot dog");
        combo1.setPrice(BigDecimal.valueOf(50.20));
        combo1.setImageUrl("https://i.imgur.com/3QXTlTP.png");
        combo1.setCategory(ConcessionProduct.ProductCategory.COMBOS);
        combo1.setCinemas(cinemaSet);

        ConcessionProduct combo2 = new ConcessionProduct();
        combo2.setName("Combo Sal");
        combo2.setDescription("Canchita mediana + Gaseosa grande + Nachos");
        combo2.setPrice(BigDecimal.valueOf(48.30));
        combo2.setImageUrl("https://i.imgur.com/mn8MkFr.png");
        combo2.setCategory(ConcessionProduct.ProductCategory.COMBOS);
        combo2.setCinemas(cinemaSet);

        ConcessionProduct combo3 = new ConcessionProduct();
        combo3.setName("Combo Familiar");
        combo3.setDescription("4 Canchitas grandes + 3 Gaseosas grandes");
        combo3.setPrice(BigDecimal.valueOf(149.00));
        combo3.setImageUrl("https://i.imgur.com/8mqvkF4.png");
        combo3.setCategory(ConcessionProduct.ProductCategory.COMBOS);
        combo3.setCinemas(cinemaSet);

        ConcessionProduct combo4 = new ConcessionProduct();
        combo4.setName("Combo Dulce");
        combo4.setDescription("Canchita grande dulce + Frugos + Nuggets x6");
        combo4.setPrice(BigDecimal.valueOf(47.40));
        combo4.setImageUrl("https://i.imgur.com/E9ZykEX.png");
        combo4.setCategory(ConcessionProduct.ProductCategory.COMBOS);
        combo4.setCinemas(cinemaSet);

        ConcessionProduct combo5 = new ConcessionProduct();
        combo5.setName("Combo Pareja");
        combo5.setDescription("2 Canchitas medianas + 2 Gaseosas grandes");
        combo5.setPrice(BigDecimal.valueOf(69.90));
        combo5.setImageUrl("https://i.imgur.com/dkPseQ3.png");
        combo5.setCategory(ConcessionProduct.ProductCategory.COMBOS);
        combo5.setCinemas(cinemaSet);

        ConcessionProduct combo6 = new ConcessionProduct();
        combo6.setName("Combo Burger Premium");
        combo6.setDescription("Canchita gigante + 2 Gaseosas grandes + Hamburguesa");
        combo6.setPrice(BigDecimal.valueOf(35.90));
        combo6.setImageUrl("https://i.imgur.com/4f5OE64.png");
        combo6.setCategory(ConcessionProduct.ProductCategory.COMBOS);
        combo6.setCinemas(cinemaSet);

        // CANCHITA
        ConcessionProduct canchita1 = new ConcessionProduct();
        canchita1.setName("Canchita Gigante Salada");
        canchita1.setDescription("Porción gigante de canchita con sal");
        canchita1.setPrice(BigDecimal.valueOf(34.90));
        canchita1.setImageUrl("https://i.imgur.com/6X0TjP9.png");
        canchita1.setCategory(ConcessionProduct.ProductCategory.CANCHITA);
        canchita1.setCinemas(cinemaSet);

        ConcessionProduct canchita2 = new ConcessionProduct();
        canchita2.setName("Canchita Grande Salada");
        canchita2.setDescription("Porción grande de canchita con sal");
        canchita2.setPrice(BigDecimal.valueOf(29.90));
        canchita2.setImageUrl("https://i.imgur.com/yoalqQt.png");
        canchita2.setCategory(ConcessionProduct.ProductCategory.CANCHITA);
        canchita2.setCinemas(cinemaSet);

        ConcessionProduct canchita3 = new ConcessionProduct();
        canchita3.setName("Canchita Grande Dulce");
        canchita3.setDescription("Porción grande de canchita dulce");
        canchita3.setPrice(BigDecimal.valueOf(31.90));
        canchita3.setImageUrl("https://i.imgur.com/4BCV5nd.png");
        canchita3.setCategory(ConcessionProduct.ProductCategory.CANCHITA);
        canchita3.setCinemas(cinemaSet);

        ConcessionProduct canchita4 = new ConcessionProduct();
        canchita4.setName("Canchita Mediana Salada");
        canchita4.setDescription("Porción mediana de canchita con sal");
        canchita4.setPrice(BigDecimal.valueOf(26.90));
        canchita4.setImageUrl("https://i.imgur.com/uJlVCc9.png");
        canchita4.setCategory(ConcessionProduct.ProductCategory.CANCHITA);
        canchita4.setCinemas(cinemaSet);

        ConcessionProduct canchita5 = new ConcessionProduct();
        canchita5.setName("Canchita Mediana Dulce");
        canchita5.setDescription("Porción mediana de canchita dulce");
        canchita5.setPrice(BigDecimal.valueOf(27.90));
        canchita5.setImageUrl("https://i.imgur.com/UCS4p8y.png");
        canchita5.setCategory(ConcessionProduct.ProductCategory.CANCHITA);
        canchita5.setCinemas(cinemaSet);

        ConcessionProduct canchita6 = new ConcessionProduct();
        canchita6.setName("Canchita Kids Salada");
        canchita6.setDescription("Porción Kids de canchita con sal");
        canchita6.setPrice(BigDecimal.valueOf(11.90));
        canchita6.setImageUrl("https://i.imgur.com/JBRSxds.png");
        canchita6.setCategory(ConcessionProduct.ProductCategory.CANCHITA);
        canchita6.setCinemas(cinemaSet);

        ConcessionProduct canchita7 = new ConcessionProduct();
        canchita7.setName("Canchita Kids Dulce");
        canchita7.setDescription("Canchita Kids dulce");
        canchita7.setPrice(BigDecimal.valueOf(12.90));
        canchita7.setImageUrl("https://i.imgur.com/Nu0Ow6U.png");
        canchita7.setCategory(ConcessionProduct.ProductCategory.CANCHITA);
        canchita7.setCinemas(cinemaSet);

        ConcessionProduct canchita8 = new ConcessionProduct();
        canchita8.setName("Canchita Mantequilla");
        canchita8.setDescription("Canchita grande con mantequilla");
        canchita8.setPrice(BigDecimal.valueOf(31.90));
        canchita8.setImageUrl("https://i.imgur.com/oSnb1b2.png");
        canchita8.setCategory(ConcessionProduct.ProductCategory.CANCHITA);
        canchita8.setCinemas(cinemaSet);

        // BEBIDAS
        ConcessionProduct bebida1 = new ConcessionProduct();
        bebida1.setName("Coca Cola Grande");
        bebida1.setDescription("Coca Cola 500ml");
        bebida1.setPrice(BigDecimal.valueOf(11.90));
        bebida1.setImageUrl("https://i.imgur.com/H0od7eK.png");
        bebida1.setCategory(ConcessionProduct.ProductCategory.BEBIDAS);
        bebida1.setCinemas(cinemaSet);

        ConcessionProduct bebida2 = new ConcessionProduct();
        bebida2.setName("Inca Kola Grande");
        bebida2.setDescription("Inca Kola 500ml");
        bebida2.setPrice(BigDecimal.valueOf(11.90));
        bebida2.setImageUrl("https://i.imgur.com/nUkZc3u.png");
        bebida2.setCategory(ConcessionProduct.ProductCategory.BEBIDAS);
        bebida2.setCinemas(cinemaSet);

        ConcessionProduct bebida3 = new ConcessionProduct();
        bebida3.setName("Sprite Grande");
        bebida3.setDescription("Sprite 500ml");
        bebida3.setPrice(BigDecimal.valueOf(11.90));
        bebida3.setImageUrl("https://i.imgur.com/YrRj40A.png");
        bebida3.setCategory(ConcessionProduct.ProductCategory.BEBIDAS);
        bebida3.setCinemas(cinemaSet);

        ConcessionProduct bebida4 = new ConcessionProduct();
        bebida4.setName("Jugo de Naranja");
        bebida4.setDescription("Jugo natural de naranja 400ml");
        bebida4.setPrice(BigDecimal.valueOf(11.90));
        bebida4.setImageUrl("https://i.imgur.com/1gzH5cZ.png");
        bebida4.setCategory(ConcessionProduct.ProductCategory.BEBIDAS);
        bebida4.setCinemas(cinemaSet);

        ConcessionProduct bebida5 = new ConcessionProduct();
        bebida5.setName("Frugos del valle");
        bebida5.setDescription("Frugos 300ml");
        bebida5.setPrice(BigDecimal.valueOf(6.90));
        bebida5.setImageUrl("https://i.imgur.com/wPEQYeE.png");
        bebida5.setCategory(ConcessionProduct.ProductCategory.BEBIDAS);
        bebida5.setCinemas(cinemaSet);

        ConcessionProduct bebida6 = new ConcessionProduct();
        bebida6.setName("Agua con Gas");
        bebida6.setDescription("Agua mineral con gas 500ml");
        bebida6.setPrice(BigDecimal.valueOf(6.90));
        bebida6.setImageUrl("https://i.imgur.com/2S1tlC6.png");
        bebida6.setCategory(ConcessionProduct.ProductCategory.BEBIDAS);
        bebida6.setCinemas(cinemaSet);

        ConcessionProduct bebida7 = new ConcessionProduct();
        bebida7.setName("Agua Sin Gas");
        bebida7.setDescription("Agua mineral 500ml");
        bebida7.setPrice(BigDecimal.valueOf(5.90));
        bebida7.setImageUrl("https://i.imgur.com/kAjLTU3.png");
        bebida7.setCategory(ConcessionProduct.ProductCategory.BEBIDAS);
        bebida7.setCinemas(cinemaSet);

        // SNACKS
        ConcessionProduct snack1 = new ConcessionProduct();
        snack1.setName("Hot Dog Frankfurter");
        snack1.setDescription("Hot dog con salchicha alemana y salsas");
        snack1.setPrice(BigDecimal.valueOf(13.90));
        snack1.setImageUrl("https://i.imgur.com/vgYqN6n.png");
        snack1.setCategory(ConcessionProduct.ProductCategory.SNACKS);
        snack1.setCinemas(cinemaSet);

        ConcessionProduct snack2 = new ConcessionProduct();
        snack2.setName("Nachos con Queso");
        snack2.setDescription("Nachos crujientes con salsa de queso");
        snack2.setPrice(BigDecimal.valueOf(14.90));
        snack2.setImageUrl("https://i.imgur.com/fmBuiPG.png");
        snack2.setCategory(ConcessionProduct.ProductCategory.SNACKS);
        snack2.setCinemas(cinemaSet);

        ConcessionProduct snack3 = new ConcessionProduct();
        snack3.setName("Papas Fritas");
        snack3.setDescription("Papas fritas crujientes porción grande");
        snack3.setPrice(BigDecimal.valueOf(7.90));
        snack3.setImageUrl("https://i.imgur.com/X5f8YC9.png");
        snack3.setCategory(ConcessionProduct.ProductCategory.SNACKS);
        snack3.setCinemas(cinemaSet);

        ConcessionProduct snack4 = new ConcessionProduct();
        snack4.setName("Tequeños x4 un");
        snack4.setDescription("4 tequeños de queso fritos");
        snack4.setPrice(BigDecimal.valueOf(10.90));
        snack4.setImageUrl("https://i.imgur.com/bDMNPBk.png");
        snack4.setCategory(ConcessionProduct.ProductCategory.SNACKS);
        snack4.setCinemas(cinemaSet);

        ConcessionProduct snack5 = new ConcessionProduct();
        snack5.setName("Nuggets x6");
        snack5.setDescription("6 nuggets de pollo crujientes");
        snack5.setPrice(BigDecimal.valueOf(13.90));
        snack5.setImageUrl("https://i.imgur.com/MtnELKD.png");
        snack5.setCategory(ConcessionProduct.ProductCategory.SNACKS);
        snack5.setCinemas(cinemaSet);

        ConcessionProduct snack6 = new ConcessionProduct();
        snack6.setName("Salchipapas");
        snack6.setDescription("Papas fritas con salchicha y salsas");
        snack6.setPrice(BigDecimal.valueOf(14.90));
        snack6.setImageUrl("https://i.imgur.com/aF0NUuv.png");
        snack6.setCategory(ConcessionProduct.ProductCategory.SNACKS);
        snack6.setCinemas(cinemaSet);

        ConcessionProduct snack7 = new ConcessionProduct();
        snack7.setName("Hamburguesa Clásica");
        snack7.setDescription("Hamburguesa con carne, lechuga y tomate");
        snack7.setPrice(BigDecimal.valueOf(16.90));
        snack7.setImageUrl("https://i.imgur.com/8U3R1Oa.png");
        snack7.setCategory(ConcessionProduct.ProductCategory.SNACKS);
        snack7.setCinemas(cinemaSet);

        ConcessionProduct snack8 = new ConcessionProduct();
        snack8.setName("Pizza Personal");
        snack8.setDescription("Pizza individual de pepperoni");
        snack8.setPrice(BigDecimal.valueOf(18.90));
        snack8.setImageUrl("https://i.imgur.com/oMEqVGb.png");
        snack8.setCategory(ConcessionProduct.ProductCategory.SNACKS);
        snack8.setCinemas(cinemaSet);

        ConcessionProduct snack9 = new ConcessionProduct();
        snack9.setName("Sandwich Club");
        snack9.setDescription("Sandwich triple con pollo, tocino y verduras");
        snack9.setPrice(BigDecimal.valueOf(16.90));
        snack9.setImageUrl("https://i.imgur.com/jwVxk1i.png");
        snack9.setCategory(ConcessionProduct.ProductCategory.SNACKS);
        snack9.setCinemas(cinemaSet);

        ConcessionProduct snack10 = new ConcessionProduct();
        snack10.setName("Alitas BBQ x7");
        snack10.setDescription("7 alitas de pollo con salsa BBQ");
        snack10.setPrice(BigDecimal.valueOf(18.90));
        snack10.setImageUrl("https://i.imgur.com/cXOORTE.png");
        snack10.setCategory(ConcessionProduct.ProductCategory.SNACKS);
        snack10.setCinemas(cinemaSet);

        ConcessionProduct snack11 = new ConcessionProduct();
        snack11.setName("Quesadilla");
        snack11.setDescription("Quesadilla de queso con guacamole");
        snack11.setPrice(BigDecimal.valueOf(15.90));
        snack11.setImageUrl("https://i.imgur.com/oK4ZOoP.png");
        snack11.setCategory(ConcessionProduct.ProductCategory.SNACKS);
        snack11.setCinemas(cinemaSet);

        ConcessionProduct snack12 = new ConcessionProduct();
        snack12.setName("Wrap de Pollo");
        snack12.setDescription("Wrap con pollo, lechuga y salsa ranch");
        snack12.setPrice(BigDecimal.valueOf(19.90));
        snack12.setImageUrl("https://i.imgur.com/VwpHY4O.png");
        snack12.setCategory(ConcessionProduct.ProductCategory.SNACKS);
        snack12.setCinemas(cinemaSet);

        // Save all products
        concessionProductRepository.saveAll(java.util.Arrays.asList(
                combo1, combo2, combo3, combo4, combo5, combo6,
                canchita1, canchita2, canchita3, canchita4, canchita5, canchita6, canchita7, canchita8,
                bebida1, bebida2, bebida3, bebida4, bebida5, bebida6, bebida7,
                snack1, snack2, snack3, snack4, snack5, snack6, snack7, snack8, snack9, snack10, snack11, snack12
        ));
    }
}