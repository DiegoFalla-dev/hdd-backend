package com.cineplus.cineplus.persistence.service.impl;

import com.cineplus.cineplus.domain.dto.CreateOrderDTO;
import com.cineplus.cineplus.domain.dto.CreateOrderItemDTO;
import com.cineplus.cineplus.domain.dto.OrderDTO;
import com.cineplus.cineplus.domain.entity.*;
import com.cineplus.cineplus.domain.repository.*;
import com.cineplus.cineplus.domain.service.OrderService;
import com.cineplus.cineplus.domain.service.PromotionService;
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
    private final UserRepository userRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final ShowtimeRepository showtimeRepository;
    private final SeatRepository seatRepository;
    private final PromotionRepository promotionRepository; // Se usa para buscar la entidad, no el DTO
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final PromotionService promotionService; // Para la validación de la lógica de negocio de promociones

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
        BigDecimal totalAmount = BigDecimal.ZERO;

        // Validar y procesar cada item de la orden
        for (CreateOrderItemDTO itemDTO : createOrderDTO.getItems()) {
            Showtime showtime = showtimeRepository.findById(itemDTO.getShowtimeId())
                    .orElseThrow(() -> new EntityNotFoundException("Función no encontrada con ID: " + itemDTO.getShowtimeId()));
            Seat seat = seatRepository.findById(itemDTO.getSeatId())
                    .orElseThrow(() -> new EntityNotFoundException("Asiento no encontrado con ID: " + itemDTO.getSeatId()));

            // Lógica de validación: ¿El asiento está disponible para esta función?
            // Podrías necesitar un método en SeatRepository para esto o verificar la relación directamente
            // Por simplicidad, asumimos que showtime.getAvailableSeats() o una lista de asientos ocupados se puede verificar aquí
            // Ejemplo: if (!seat.getShowtimes().contains(showtime) || !seatIsAvailable(seat, showtime)) ...

            // Aquí se valida si el asiento ya está ocupado para esta función y si su ID está asociado a la función
            if (showtime.getAvailableSeats() <= 0) {
                throw new IllegalStateException("No hay asientos disponibles para la función con ID: " + showtime.getId());
            }

            // Verificar si el asiento ya ha sido comprado para esta función
            if (orderItemRepository.findByShowtimeAndSeatAndTicketStatus(showtime, seat, TicketStatus.VALID).isPresent()) {
                throw new IllegalStateException("El asiento " + seat.getSeatIdentifier() + " ya está ocupado para la función " + showtime.getId());
            }

            // Reducir la disponibilidad de asientos
            showtime.setAvailableSeats(showtime.getAvailableSeats() - 1);
            showtimeRepository.save(showtime); // Guarda el cambio en la disponibilidad

            // Crear el OrderItem
            OrderItem orderItem = OrderItem.builder()
                    .showtime(showtime)
                    .seat(seat)
                    .price(itemDTO.getPrice())
                    .ticketStatus(TicketStatus.VALID)
                    .build();
            newOrderItems.add(orderItem);
            totalAmount = totalAmount.add(itemDTO.getPrice());
        }

        Promotion appliedPromotion = null;
        if (createOrderDTO.getPromotionCode() != null && !createOrderDTO.getPromotionCode().isEmpty()) {
            // Verificar si la promoción es válida y aplicable
            if (promotionService.isValidPromotionForAmount(createOrderDTO.getPromotionCode(), totalAmount)) {
                // Obtener la entidad Promotion (no el DTO)
                Optional<Promotion> promoEntityOpt = promotionRepository.findByCode(createOrderDTO.getPromotionCode());
                if (promoEntityOpt.isPresent()) {
                    appliedPromotion = promoEntityOpt.get();
                    totalAmount = applyPromotionDiscount(totalAmount, appliedPromotion);
                    // Incrementar el contador de usos de la promoción
                    appliedPromotion.setCurrentUses(appliedPromotion.getCurrentUses() + 1);
                    promotionRepository.save(appliedPromotion); // Guardar la promoción actualizada
                }
            } else {
                // Opcional: Lanzar una excepción o loggear si la promoción no es válida
                System.out.println("Promoción no válida o no aplicable: " + createOrderDTO.getPromotionCode());
            }
        }

        // Crear la entidad Order
        Order order = Order.builder()
                .user(user)
                .orderDate(LocalDateTime.now())
                .totalAmount(totalAmount)
                .paymentMethod(paymentMethod)
                .orderStatus(OrderStatus.COMPLETED) // O PENDING si hay un proceso de confirmación de pago
                .invoiceNumber(UUID.randomUUID().toString().substring(0, 8).toUpperCase()) // Generar un número de factura simple
                .promotion(appliedPromotion)
                .build();

        // Asignar la orden a cada item y guardar
        order.setOrderItems(newOrderItems); // Asigna la lista antes de guardar para cascadear
        for (OrderItem item : newOrderItems) {
            item.setOrder(order);
        }

        Order savedOrder = orderRepository.save(order);

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


        return orderMapper.toDto(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDTO previewOrder(CreateOrderDTO createOrderDTO) {
        // Reutiliza lógica parcial de createOrder sin persistir entidades ni modificar disponibilidad.
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CreateOrderItemDTO itemDTO : createOrderDTO.getItems()) {
            totalAmount = totalAmount.add(itemDTO.getPrice());
        }
        Promotion appliedPromotion = null;
        if (createOrderDTO.getPromotionCode() != null && !createOrderDTO.getPromotionCode().isBlank()) {
            Optional<Promotion> promoEntityOpt = promotionRepository.findByCode(createOrderDTO.getPromotionCode());
            if (promoEntityOpt.isPresent() && promotionService.isValidPromotionForAmount(createOrderDTO.getPromotionCode(), totalAmount)) {
                appliedPromotion = promoEntityOpt.get();
                totalAmount = applyPromotionDiscount(totalAmount, appliedPromotion);
            }
        }
        OrderDTO dto = new OrderDTO();
        dto.setTotalAmount(totalAmount);
        dto.setPromotionCode(appliedPromotion != null ? appliedPromotion.getCode() : null);
        dto.setOrderStatus(OrderStatus.PENDING);
        return dto;
    }

    @Override
    @Transactional
    public Optional<OrderDTO> cancelOrder(Long id) {
        return orderRepository.findById(id).map(order -> {
            if (order.getOrderStatus() == OrderStatus.COMPLETED || order.getOrderStatus() == OrderStatus.CANCELLED) {
                // No cancelar si ya está completada o cancelada
                return orderMapper.toDto(order);
            }
            order.setOrderStatus(OrderStatus.CANCELLED);
            return orderMapper.toDto(orderRepository.save(order));
        });
    }

    private BigDecimal applyPromotionDiscount(BigDecimal totalAmount, Promotion promotion) {
        if (promotion.getDiscountType() == DiscountType.PERCENTAGE) {
            // Ejemplo: 10% de descuento -> 0.10
            BigDecimal discountFactor = promotion.getValue();
            return totalAmount.subtract(totalAmount.multiply(discountFactor));
        } else if (promotion.getDiscountType() == DiscountType.FIXED_AMOUNT) {
            return totalAmount.subtract(promotion.getValue()).max(BigDecimal.ZERO); // Asegurarse de que no sea negativo
        }
        return totalAmount; // No hay descuento aplicado
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