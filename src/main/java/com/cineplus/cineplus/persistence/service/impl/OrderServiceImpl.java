package com.cineplus.cineplus.persistence.service.impl;

import com.cineplus.cineplus.domain.dto.CreateOrderDTO;
import com.cineplus.cineplus.domain.dto.CreateOrderItemDTO;
import com.cineplus.cineplus.domain.dto.CreateOrderConcessionDTO;
import com.cineplus.cineplus.domain.dto.OrderDTO;
import com.cineplus.cineplus.domain.entity.*;
import com.cineplus.cineplus.domain.repository.*;
import com.cineplus.cineplus.domain.service.OrderService;
import com.cineplus.cineplus.domain.service.PromotionService;
import com.cineplus.cineplus.domain.service.InvoiceService;
import com.cineplus.cineplus.domain.service.MailService;
import com.cineplus.cineplus.domain.service.PriceCalculationUtil;
import com.cineplus.cineplus.persistence.mapper.OrderItemMapper;
import com.cineplus.cineplus.persistence.mapper.OrderMapper;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.io.image.ImageDataFactory;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderConcessionRepository orderConcessionRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final ShowtimeRepository showtimeRepository;
    private final SeatRepository seatRepository;
    private final PromotionRepository promotionRepository; // Se usa para buscar la entidad, no el DTO
    private final TicketTypeRepository ticketTypeRepository;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final PromotionService promotionService; // Para la validación de la lógica de negocio de promociones
    private final com.cineplus.cineplus.domain.service.TicketPriceService ticketPriceService;
    private final InvoiceService invoiceService;
    private final MailService mailService;

    private static final String QR_CODE_BASE_URL = "http://localhost:8080/api/tickets/"; // URL base para los tickets

    @Override
    @Transactional(readOnly = true)
    public List<OrderDTO> getAllOrders() {
        return orderMapper.toDtoList(orderRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OrderDTO> getOrderById(Long id) {
        return orderRepository.findById(id).map(orderMapper::toDto);
    }

    @Override
    @Transactional
    public OrderDTO createOrder(CreateOrderDTO createOrderDTO) {
        User user = userRepository.findById(createOrderDTO.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con ID: " + createOrderDTO.getUserId()));
        PaymentMethod paymentMethod = paymentMethodRepository.findById(createOrderDTO.getPaymentMethodId())
                .orElseThrow(() -> new EntityNotFoundException("Método de pago no encontrado con ID: " + createOrderDTO.getPaymentMethodId()));

        List<OrderItem> newOrderItems = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO; // Cambiar a subtotal (sin IGV)

        // Validar y procesar cada item de la orden
        for (CreateOrderItemDTO itemDTO : createOrderDTO.getItems()) {
            Showtime showtime = showtimeRepository.findById(itemDTO.getShowtimeId())
                    .orElseThrow(() -> new EntityNotFoundException("Función no encontrada con ID: " + itemDTO.getShowtimeId()));
            Seat seat = seatRepository.findById(itemDTO.getSeatId())
                    .orElseThrow(() -> new EntityNotFoundException("Asiento no encontrado con ID: " + itemDTO.getSeatId()));

            // === VALIDACIONES MEJORADAS ===
            
            // 1. Validar que la función no haya pasado
            LocalDateTime showtimeDateTime = LocalDateTime.of(showtime.getDate(), showtime.getTime());
            if (showtimeDateTime.isBefore(LocalDateTime.now())) {
                throw new IllegalStateException("La función ya ha pasado. No se pueden comprar entradas para funciones anteriores.");
            }
            
            // 2. Validar que la función no sea en menos de 15 minutos (tiempo de procesamiento)
            if (showtimeDateTime.isBefore(LocalDateTime.now().plusMinutes(15))) {
                throw new IllegalStateException("No se pueden comprar entradas para funciones que comienzan en menos de 15 minutos.");
            }
            
            // 3. Validar disponibilidad global de asientos
            if (showtime.getAvailableSeats() <= 0) {
                throw new IllegalStateException("No hay asientos disponibles para la función con ID: " + showtime.getId());
            }

            // 4. Verificar si el asiento ya ha sido comprado para esta función
            if (orderItemRepository.findByShowtimeAndSeatAndTicketStatus(showtime, seat, TicketStatus.VALID).isPresent()) {
                throw new IllegalStateException("El asiento " + seat.getSeatIdentifier() + " ya está ocupado para la función " + showtime.getId());
            }
            
            // 5. Validar que el asiento pertenezca a la sala de la función
            if (!seat.getShowtime().getTheater().getId().equals(showtime.getTheater().getId())) {
                throw new IllegalStateException("El asiento " + seat.getSeatIdentifier() + " no pertenece a la sala de esta función.");
            }

            // Reducir la disponibilidad de asientos
            showtime.setAvailableSeats(showtime.getAvailableSeats() - 1);
            showtimeRepository.save(showtime); // Guarda el cambio en la disponibilidad

            // Determinar precio oficial del ticket: si se envió ticketType, buscar su precio en el catálogo.
            BigDecimal effectivePrice = itemDTO.getPrice();
            if (itemDTO.getTicketType() != null && !itemDTO.getTicketType().isEmpty()) {
                TicketType tt = ticketTypeRepository.findByCodeIgnoreCase(itemDTO.getTicketType()).orElse(null);
                if (tt != null) {
                    effectivePrice = tt.getPrice();
                    // Sobre-escribimos el precio enviado por el cliente con el precio oficial
                } else {
                    System.out.println("Warning: ticketType not found: " + itemDTO.getTicketType() + ", using provided price");
                }
            }

            // Crear el OrderItem con el precio efectivo
            OrderItem orderItem = OrderItem.builder()
                    .showtime(showtime)
                    .seat(seat)
                    .price(effectivePrice)
                    .ticketType(itemDTO.getTicketType()) // Guardar el tipo de entrada
                    .ticketStatus(TicketStatus.VALID)
                    .build();
            newOrderItems.add(orderItem);
            subtotal = subtotal.add(effectivePrice); // Agregar al subtotal
        }

        // Procesar concesiones (dulcería) si existen
        List<OrderConcession> newOrderConcessions = new ArrayList<>();
        if (createOrderDTO.getConcessions() != null && !createOrderDTO.getConcessions().isEmpty()) {
            for (CreateOrderConcessionDTO concessionDTO : createOrderDTO.getConcessions()) {
                Product product = productRepository.findById(concessionDTO.getProductId())
                        .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado con ID: " + concessionDTO.getProductId()));
                
                // === VALIDACIONES DE INVENTARIO ===
                
                // 1. Validar que el producto esté activo
                if (!product.getAvailable()) {
                    throw new IllegalStateException("El producto '" + product.getName() + "' no está disponible para la venta.");
                }
                
                // 2. Validar cantidad solicitada
                if (concessionDTO.getQuantity() <= 0) {
                    throw new IllegalArgumentException("La cantidad del producto debe ser mayor a 0.");
                }
                
                BigDecimal totalPrice = concessionDTO.getUnitPrice().multiply(BigDecimal.valueOf(concessionDTO.getQuantity()));
                
                OrderConcession orderConcession = OrderConcession.builder()
                        .product(product)
                        .quantity(concessionDTO.getQuantity())
                        .unitPrice(concessionDTO.getUnitPrice())
                        .totalPrice(totalPrice)
                        .build();
                newOrderConcessions.add(orderConcession);
                subtotal = subtotal.add(totalPrice); // Agregar al subtotal
            }
        }

        // === APLICACIÓN DE DESCUENTOS Y CÁLCULO DE IGV ===
        BigDecimal discountAmount = BigDecimal.ZERO;
        Promotion appliedPromotion = null;
        
            // Aplicar descuento por puntos de fidelización si viene en el payload
            if (createOrderDTO.getFidelityDiscountAmount() != null) {
                discountAmount = discountAmount.add(createOrderDTO.getFidelityDiscountAmount());
            }
        if (createOrderDTO.getPromotionCode() != null && !createOrderDTO.getPromotionCode().isEmpty()) {
            // Verificar si la promoción es válida y aplicable
            if (promotionService.isValidPromotionForAmount(createOrderDTO.getPromotionCode(), subtotal)) {
                // Obtener la entidad Promotion (no el DTO)
                Optional<Promotion> promoEntityOpt = promotionRepository.findByCode(createOrderDTO.getPromotionCode());
                if (promoEntityOpt.isPresent()) {
                    appliedPromotion = promoEntityOpt.get();
                        discountAmount = discountAmount.add(applyPromotionDiscount(subtotal, appliedPromotion));
                    // Incrementar el contador de usos de la promoción
                    appliedPromotion.setCurrentUses(appliedPromotion.getCurrentUses() + 1);
                    
                    // Actualizar is_active basado en el tipo de promoción
                    if (appliedPromotion.getMaxUses() != null) {
                        // Si es promoción de un solo uso (maxUses == 1), marcarla como inactiva
                        if (appliedPromotion.getMaxUses() == 1) {
                            appliedPromotion.setIsActive(false);
                        }
                        // Si es promoción de múltiples usos, marcarla como inactiva solo cuando alcance el límite
                        else if (appliedPromotion.getMaxUses() > 1 && appliedPromotion.getCurrentUses() >= appliedPromotion.getMaxUses()) {
                            appliedPromotion.setIsActive(false);
                        }
                    }
                    
                    promotionRepository.save(appliedPromotion); // Guardar la promoción actualizada
                }
            } else {
                // Opcional: Lanzar una excepción o loggear si la promoción no es válida
                System.out.println("Promoción no válida o no aplicable: " + createOrderDTO.getPromotionCode());
            }
        }
        
        // Usar la utilidad centralizada de cálculo
        PriceCalculationUtil.PriceBreakdown breakdown = PriceCalculationUtil.calculatePriceBreakdown(subtotal, discountAmount);
        BigDecimal totalAmount = breakdown.getGrandTotal(); // Total CON IGV

        // Crear la entidad Order
        // Nota: invoiceNumber se asignará después, una vez que se genere la factura
        Order order = Order.builder()
                .user(user)
                .orderDate(LocalDateTime.now())
                .subtotalAmount(subtotal) // Guardar el subtotal
                .taxAmount(breakdown.getTaxAmount()) // Guardar el IGV
                .totalAmount(totalAmount) // Total CON IGV
                .paymentMethod(paymentMethod)
                .orderStatus(OrderStatus.COMPLETED) // O PENDING si hay un proceso de confirmación de pago
                .invoiceNumber(null) // Se asignará después con el número de factura
                .promotion(appliedPromotion)
                .build();

        // Asignar la orden a cada item y guardar
        order.setOrderItems(newOrderItems); // Asigna la lista antes de guardar para cascadear
        for (OrderItem item : newOrderItems) {
            item.setOrder(order);
        }

        // Asignar la orden a cada concesión
        order.setOrderConcessions(newOrderConcessions);
        for (OrderConcession concession : newOrderConcessions) {
            concession.setOrder(order);
        }

        Order savedOrder = orderRepository.save(order);

        // --- SISTEMA DE FIDELIZACIÓN ---
        // Calcular puntos: 1 punto cada S/. 10 gastados (usando el total con IGV)
        Integer pointsEarned = totalAmount.divide(BigDecimal.TEN, RoundingMode.DOWN).intValue();
        user.setFidelityPoints(user.getFidelityPoints() + pointsEarned);
        user.setLastPurchaseDate(LocalDateTime.now());
        userRepository.save(user);
        
        // Log de puntos agregados
        if (pointsEarned > 0) {
            System.out.println("Puntos de fidelización: " + pointsEarned + " puntos agregados al usuario " + user.getId());
        }

        // Persistir líneas de precio de tickets agrupando por showtimeId + unitPrice
        try {
            // Agrupar items entrantes por showtimeId y price
            java.util.Map<String, com.cineplus.cineplus.domain.dto.TicketPriceDto> agg = new java.util.HashMap<>();
            for (CreateOrderItemDTO itemDTO : createOrderDTO.getItems()) {
                String ttype = itemDTO.getTicketType() == null ? "" : itemDTO.getTicketType();
                // Determinar precio efectivo (misma lógica que al crear OrderItem)
                BigDecimal effectivePrice = itemDTO.getPrice();
                if (itemDTO.getTicketType() != null && !itemDTO.getTicketType().isEmpty()) {
                    TicketType tt = ticketTypeRepository.findByCodeIgnoreCase(itemDTO.getTicketType()).orElse(null);
                    if (tt != null) {
                        effectivePrice = tt.getPrice();
                    }
                }
                String key = itemDTO.getShowtimeId() + "|" + effectivePrice.toPlainString() + "|" + ttype;
                com.cineplus.cineplus.domain.dto.TicketPriceDto current = agg.get(key);
                if (current == null) {
                    com.cineplus.cineplus.domain.dto.TicketPriceDto dto = com.cineplus.cineplus.domain.dto.TicketPriceDto.builder()
                            .showtimeId(itemDTO.getShowtimeId())
                            .unitPrice(effectivePrice)
                            .quantity(1)
                            .subtotal(effectivePrice)
                            .ticketType(ttype == null || ttype.isEmpty() ? null : ttype)
                            .build();
                    agg.put(key, dto);
                } else {
                    current.setQuantity(current.getQuantity() + 1);
                    current.setSubtotal(current.getUnitPrice().multiply(BigDecimal.valueOf(current.getQuantity())));
                }
            }
            List<com.cineplus.cineplus.domain.dto.TicketPriceDto> toSave = new ArrayList<>(agg.values());
            if (!toSave.isEmpty()) {
                ticketPriceService.saveForOrder(savedOrder.getId(), toSave);
            }
        } catch (Exception ex) {
            // No interrumpir el flujo de creación de la orden por un fallo en el guardado de líneas de precio,
            // pero logueamos el error para depuración.
            System.err.println("Error guardando TicketPrice para order " + savedOrder.getId() + ": " + ex.getMessage());
        }

        // Generar QRs y PDFs después de guardar la orden y los items
        for (OrderItem item : savedOrder.getOrderItems()) {
            try {
                // Generar QR para cada ticket
                String ticketData = QR_CODE_BASE_URL + item.getId();
                byte[] qrCodeImage = generateQrCodeImage(ticketData, 200, 200);
                // Aquí deberías guardar qrCodeImage en un almacenamiento de archivos (S3, sistema de archivos, etc.)
                // Por ahora, solo guardamos una URL ficticia
                item.setQrCodeTicketUrl("/qrcodes/ticket_" + item.getId() + ".png");

                // Generar PDF para cada ticket
                byte[] ticketPdfBytes = generateTicketPdfContent(item);
                // Guardar ticketPdfBytes en almacenamiento
                item.setTicketPdfUrl("/pdfs/ticket_" + item.getId() + ".pdf");

                orderItemRepository.save(item); // Actualizar el item con las URLs generadas
            } catch (Exception e) {
                System.err.println("Error al generar QR/PDF para OrderItem " + item.getId() + ": " + e.getMessage());
                // Considerar cómo manejar este error (revertir transacción, marcar como error, etc.)
            }
        }

        // Generar PDF de factura para la orden completa
        try {
            byte[] invoicePdfBytes = generateInvoicePdfContent(savedOrder);
            // Guardar invoicePdfBytes en almacenamiento
            savedOrder.setInvoicePdfUrl("/pdfs/invoice_" + savedOrder.getId() + ".pdf");
            orderRepository.save(savedOrder); // Actualizar la orden con la URL de la factura
        } catch (Exception e) {
            System.err.println("Error al generar PDF de factura para Order " + savedOrder.getId() + ": " + e.getMessage());
        }

        // --- GENERACIÓN DE FACTURA SIMULADA (SUNAT MOCK) ---
        try {
            InvoiceService.InvoiceResult invoiceResult = invoiceService.generateInvoice(
                savedOrder.getId(),
                "20123456789",  // RUC empresa
                "B001"          // Serie
            );
            
            if (invoiceResult.success) {
                // Guardar número de factura en la orden
                savedOrder.setInvoiceNumber(invoiceResult.invoiceNumber);
                orderRepository.save(savedOrder);
                System.out.println("[ORDER] ✓ Factura generada: " + invoiceResult.invoiceNumber);
                System.out.println("[ORDER] QR Data: " + invoiceResult.qrCode);
            }
        } catch (Exception e) {
            System.err.println("[ORDER-ERROR] No se pudo generar factura: " + e.getMessage());
        }

        // --- ENVÍO DE EMAIL DE CONFIRMACIÓN ---
        try {
            mailService.sendOrderConfirmation(savedOrder);
            System.out.println("[ORDER] ✓ Email de confirmación enviado a " + user.getEmail());
        } catch (Exception e) {
            System.err.println("[ORDER-ERROR] No se pudo enviar email de confirmación: " + e.getMessage());
        }

        return orderMapper.toDto(savedOrder);
    }

    private BigDecimal applyPromotionDiscount(BigDecimal totalAmount, Promotion promotion) {
        if (promotion.getDiscountType() == DiscountType.PERCENTAGE) {
            // El value es el porcentaje (ej: 10 para 10%). Dividir entre 100 para obtener el factor decimal
            BigDecimal discountFactor = promotion.getValue().divide(BigDecimal.valueOf(100), 4, java.math.RoundingMode.HALF_UP);
            BigDecimal discountAmount = totalAmount.multiply(discountFactor);
            return discountAmount;
        } else if (promotion.getDiscountType() == DiscountType.FIXED_AMOUNT) {
            // Para monto fijo, retornar el valor directamente, pero no puede ser mayor que el total
            return promotion.getValue().min(totalAmount);
        }
        return BigDecimal.ZERO; // No hay descuento aplicado
    }


    @Override
    @Transactional
    public Optional<OrderDTO> updateOrderStatus(Long id, OrderStatus newStatus) {
        return orderRepository.findById(id).map(order -> {
            order.setOrderStatus(newStatus);
            return orderMapper.toDto(orderRepository.save(order));
        });
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDTO> getOrdersByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con ID: " + userId));
        return orderMapper.toDtoList(orderRepository.findByUser(user));
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] generateInvoicePdf(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Orden no encontrada con ID: " + orderId));
        try {
            return generateInvoicePdfContent(order);
        } catch (Exception e) {
            throw new RuntimeException("Error al generar PDF de la factura: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] generateTicketQrCode(Long orderItemId) {
        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new EntityNotFoundException("Item de orden no encontrado con ID: " + orderItemId));
        try {
            String ticketData = QR_CODE_BASE_URL + orderItem.getId();
            return generateQrCodeImage(ticketData, 300, 300);
        } catch (Exception e) {
            throw new RuntimeException("Error al generar QR para el ticket: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] generateTicketPdf(Long orderItemId) {
        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new EntityNotFoundException("Item de orden no encontrado con ID: " + orderItemId));
        try {
            return generateTicketPdfContent(orderItem);
        } catch (Exception e) {
            throw new RuntimeException("Error al generar PDF del ticket: " + e.getMessage(), e);
        }
    }

    // --- Métodos de utilidad para generar QR y PDF ---
    private byte[] generateQrCodeImage(String text, int width, int height) throws Exception {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);
        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
        return pngOutputStream.toByteArray();
    }

    private byte[] generateInvoicePdfContent(Order order) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        document.add(new Paragraph("Factura de Compra")
                .setFontSize(24)
                .setTextAlignment(TextAlignment.CENTER)
                .setBold());
        document.add(new Paragraph("CinePlus").setFontSize(18).setTextAlignment(TextAlignment.CENTER));
        document.add(new Paragraph("--------------------------------------------------"));

        document.add(new Paragraph("Número de Factura: " + order.getInvoiceNumber()));
        document.add(new Paragraph("Fecha de Compra: " + order.getOrderDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))));
        document.add(new Paragraph("Usuario: " + order.getUser().getFirstName() + " " + order.getUser().getLastName()));
        document.add(new Paragraph("Método de Pago (Titular): " + order.getPaymentMethod().getCardHolderEncrypted()));
        document.add(new Paragraph("Estado: " + order.getOrderStatus()));
        document.add(new Paragraph("--------------------------------------------------"));

        document.add(new Paragraph("Detalle de Items:").setBold());
        for (OrderItem item : order.getOrderItems()) {
            document.add(new Paragraph(String.format("- Película: %s, Función: %s, Asiento: %s, Precio: $%.2f",
                    item.getShowtime().getMovie().getTitle(),
                    item.getShowtime().getFormat() + " " + item.getShowtime().getTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                    item.getSeat().getSeatIdentifier(),
                    item.getPrice())));
        }
        document.add(new Paragraph("--------------------------------------------------"));
        if (order.getPromotion() != null) {
            document.add(new Paragraph(String.format("Promoción Aplicada (%s): %s",
                    order.getPromotion().getCode(), order.getPromotion().getDescription())));
        }
        document.add(new Paragraph(String.format("Total: $%.2f", order.getTotalAmount()))
                .setFontSize(16)
                .setBold()
                .setTextAlignment(TextAlignment.RIGHT));

        document.close();
        return baos.toByteArray();
    }

    private byte[] generateTicketPdfContent(OrderItem orderItem) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        document.add(new Paragraph("Ticket de Entrada CinePlus")
                .setFontSize(20)
                .setTextAlignment(TextAlignment.CENTER)
                .setBold());
        document.add(new Paragraph("--------------------------------------------------"));

        document.add(new Paragraph("Película: " + orderItem.getShowtime().getMovie().getTitle()).setBold());
        document.add(new Paragraph("Cine: " + orderItem.getShowtime().getTheater().getCinema().getName()));
        document.add(new Paragraph("Sala: " + orderItem.getShowtime().getTheater().getName()));
        document.add(new Paragraph("Fecha: " + orderItem.getShowtime().getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
        document.add(new Paragraph("Hora: " + orderItem.getShowtime().getTime().format(DateTimeFormatter.ofPattern("HH:mm"))));
        document.add(new Paragraph("Formato: " + orderItem.getShowtime().getFormat()));
        document.add(new Paragraph("Asiento: " + orderItem.getSeat().getSeatIdentifier()).setBold());
        document.add(new Paragraph(String.format("Precio: $%.2f", orderItem.getPrice())));
        document.add(new Paragraph("Estado: " + orderItem.getTicketStatus()));
        document.add(new Paragraph("--------------------------------------------------"));

        // Añadir el QR al PDF
        if (orderItem.getQrCodeTicketUrl() != null) {
            // En un caso real, aquí cargarías la imagen QR guardada en la URL,
            // o la generarías al vuelo si no la guardas como archivo.
            // Para este ejemplo, la generamos al vuelo.
            byte[] qrImageBytes = generateQrCodeImage(QR_CODE_BASE_URL + orderItem.getId(), 150, 150);
            Image qrImage = new Image(ImageDataFactory.create(qrImageBytes));
            document.add(qrImage.setTextAlignment(TextAlignment.CENTER));
        }

        document.add(new Paragraph("¡Disfruta tu función!").setTextAlignment(TextAlignment.CENTER));

        document.close();
        return baos.toByteArray();
    }
}