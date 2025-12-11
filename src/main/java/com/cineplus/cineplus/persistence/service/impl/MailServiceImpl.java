package com.cineplus.cineplus.persistence.service.impl;

import com.cineplus.cineplus.domain.entity.Order;
import com.cineplus.cineplus.domain.service.MailService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.internet.MimeMessage;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    private static final Logger log = LoggerFactory.getLogger(MailServiceImpl.class);
    private final JavaMailSender mailSender;

    @Value("${app.mail.from:no-reply@cineplus.local}")
    private String from;

    @Override
    public void sendOrderConfirmation(Order order) {
        try {
            if (order == null || order.getUser() == null || order.getUser().getEmail() == null) {
                log.warn("[EMAIL] No se puede enviar correo: orden o email de usuario nulo");
                return;
            }

            String userEmail = order.getUser().getEmail();
            String userName = order.getUser().getFirstName() + " " + order.getUser().getLastName();
            
            // Crear HTML mejorado
            String htmlContent = buildOrderConfirmationHtml(order, userName);
            
            try {
                // Intentar enviar con MIME para HTML (para MailHog)
                MimeMessage mimeMessage = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
                helper.setFrom(from);
                helper.setTo(userEmail);
                helper.setSubject("✓ Confirmación de compra #" + order.getId() + " - CINEPLUS");
                helper.setText(htmlContent, true);
                
                mailSender.send(mimeMessage);
                log.info("[EMAIL] Correo HTML de confirmación enviado a {} para orden {}", userEmail, order.getId());
            } catch (Exception mimeEx) {
                // Fallback a SimpleMailMessage si falla MIME
                log.warn("[EMAIL] MIME falló, usando fallback simple: {}", mimeEx.getMessage());
                SimpleMailMessage msg = new SimpleMailMessage();
                msg.setFrom(from);
                msg.setTo(userEmail);
                msg.setSubject("Confirmación de compra #" + order.getId());
                msg.setText(buildOrderConfirmationText(order, userName));
                mailSender.send(msg);
                log.info("[EMAIL] Correo simple enviado a {} para orden {}", userEmail, order.getId());
            }
        } catch (Exception e) {
            log.warn("[EMAIL] No se pudo enviar correo de confirmación: {} - {}", e.getClass().getSimpleName(), e.getMessage());
        }
    }
    
    private String buildOrderConfirmationHtml(Order order, String userName) {
        String dateFormatted = order.getOrderDate() != null 
            ? order.getOrderDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
            : "N/A";
        
        return "<!DOCTYPE html>\n" +
            "<html>\n" +
            "<head>\n" +
            "  <meta charset=\"UTF-8\">\n" +
            "  <style>\n" +
            "    body { font-family: Arial, sans-serif; background-color: #f5f5f5; }\n" +
            "    .container { max-width: 600px; margin: 20px auto; background: white; padding: 30px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }\n" +
            "    .header { background: linear-gradient(135deg, #1a1a1a 0%, #2d2d2d 100%); color: white; padding: 20px; border-radius: 4px; text-align: center; }\n" +
            "    .header h1 { margin: 0; font-size: 28px; }\n" +
            "    .header p { margin: 5px 0 0 0; color: #ccc; }\n" +
            "    .section { margin: 20px 0; padding: 15px; background: #f9f9f9; border-left: 4px solid #ff6b35; }\n" +
            "    .section h3 { margin: 0 0 10px 0; color: #1a1a1a; }\n" +
            "    .info-row { display: flex; justify-content: space-between; padding: 8px 0; border-bottom: 1px solid #eee; }\n" +
            "    .info-row:last-child { border-bottom: none; }\n" +
            "    .info-label { font-weight: bold; color: #555; }\n" +
            "    .info-value { color: #333; }\n" +
            "    .total { font-size: 24px; font-weight: bold; color: #ff6b35; text-align: right; }\n" +
            "    .footer { text-align: center; color: #999; font-size: 12px; margin-top: 20px; border-top: 1px solid #eee; padding-top: 15px; }\n" +
            "  </style>\n" +
            "</head>\n" +
            "<body>\n" +
            "  <div class=\"container\">\n" +
            "    <div class=\"header\">\n" +
            "      <h1>🎬 CINEPLUS</h1>\n" +
            "      <p>Tu experiencia cinematográfica premium</p>\n" +
            "    </div>\n" +
            "    <h2>¡Gracias por tu compra, " + userName + "!</h2>\n" +
            "    <div class=\"section\">\n" +
            "      <h3>Detalles de tu Orden</h3>\n" +
            "      <div class=\"info-row\">\n" +
            "        <span class=\"info-label\">Número de Orden:</span>\n" +
            "        <span class=\"info-value\">#" + order.getId() + "</span>\n" +
            "      </div>\n" +
            "      <div class=\"info-row\">\n" +
            "        <span class=\"info-label\">Estado:</span>\n" +
            "        <span class=\"info-value\">" + order.getOrderStatus() + "</span>\n" +
            "      </div>\n" +
            "      <div class=\"info-row\">\n" +
            "        <span class=\"info-label\">Fecha:</span>\n" +
            "        <span class=\"info-value\">" + dateFormatted + "</span>\n" +
            "      </div>\n" +
            "      <div class=\"info-row\">\n" +
            "        <span class=\"info-label\">Total:</span>\n" +
            "        <span class=\"total\">S/ " + order.getTotalAmount() + "</span>\n" +
            "      </div>\n" +
            "    </div>\n" +
            "    <div class=\"section\">\n" +
            "      <h3>Próximos Pasos</h3>\n" +
            "      <p>Tu ticket está listo. Puedes descargar el PDF desde tu cuenta en CINEPLUS.</p>\n" +
            "      <p>Presenta tu código QR en la entrada del cine.</p>\n" +
            "    </div>\n" +
            "    <div class=\"footer\">\n" +
            "      <p>Si tienes preguntas, contacta a nuestro soporte.</p>\n" +
            "      <p>&copy; 2024 CINEPLUS - Todos los derechos reservados</p>\n" +
            "    </div>\n" +
            "  </div>\n" +
            "</body>\n" +
            "</html>";
    }
    
    private String buildOrderConfirmationText(Order order, String userName) {
        return "Hola " + userName + ",\n\n" +
            "Gracias por tu compra en CINEPLUS.\n\n" +
            "Detalles de tu orden:\n" +
            "- Número de orden: #" + order.getId() + "\n" +
            "- Estado: " + order.getOrderStatus() + "\n" +
            "- Total: S/ " + order.getTotalAmount() + "\n\n" +
            "Tu ticket está listo. Descárgalo desde tu cuenta y presenta el código QR en la entrada del cine.\n\n" +
            "¡Que disfrutes tu película!\n\n" +
            "CINEPLUS\n" +
            "Tu experiencia cinematográfica premium";
    }
    
    @Override
    public void sendPasswordResetEmail(String userEmail, String userName, String resetToken) {
        try {
            if (userEmail == null || resetToken == null) {
                log.warn("[EMAIL] No se puede enviar correo de recuperación: datos nulos");
                return;
            }
            
            String resetLink = "http://localhost:5173/reset-password?token=" + resetToken;
            String htmlContent = buildPasswordResetHtml(userName, resetLink);
            
            try {
                MimeMessage mimeMessage = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
                helper.setFrom(from);
                helper.setTo(userEmail);
                helper.setSubject("🔒 Recuperación de Contraseña - CINEPLUS");
                helper.setText(htmlContent, true);
                
                mailSender.send(mimeMessage);
                log.info("[EMAIL] Correo de recuperación de contraseña enviado a {}", userEmail);
            } catch (Exception mimeEx) {
                log.warn("[EMAIL] MIME falló para recuperación, usando fallback simple: {}", mimeEx.getMessage());
                SimpleMailMessage msg = new SimpleMailMessage();
                msg.setFrom(from);
                msg.setTo(userEmail);
                msg.setSubject("Recuperación de Contraseña - CINEPLUS");
                msg.setText("Hola " + userName + ",\n\n" +
                        "Has solicitado restablecer tu contraseña.\n\n" +
                        "Haz clic en el siguiente enlace para crear una nueva contraseña:\n" +
                        resetLink + "\n\n" +
                        "Este enlace expira en 1 hora.\n\n" +
                        "Si no solicitaste este cambio, ignora este mensaje.\n\n" +
                        "CINEPLUS");
                mailSender.send(msg);
                log.info("[EMAIL] Correo simple de recuperación enviado a {}", userEmail);
            }
        } catch (Exception e) {
            log.warn("[EMAIL] No se pudo enviar correo de recuperación: {} - {}", e.getClass().getSimpleName(), e.getMessage());
        }
    }
    
    @Override
    public void sendWelcomeEmail(String userEmail, String userName) {
        try {
            if (userEmail == null) {
                log.warn("[EMAIL] No se puede enviar correo de bienvenida: email nulo");
                return;
            }
            
            String htmlContent = buildWelcomeHtml(userName);
            
            try {
                MimeMessage mimeMessage = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
                helper.setFrom(from);
                helper.setTo(userEmail);
                helper.setSubject("🎬 ¡Bienvenido a CINEPLUS!");
                helper.setText(htmlContent, true);
                
                mailSender.send(mimeMessage);
                log.info("[EMAIL] Correo de bienvenida enviado a {}", userEmail);
            } catch (Exception mimeEx) {
                log.warn("[EMAIL] MIME falló para bienvenida, usando fallback simple: {}", mimeEx.getMessage());
                SimpleMailMessage msg = new SimpleMailMessage();
                msg.setFrom(from);
                msg.setTo(userEmail);
                msg.setSubject("¡Bienvenido a CINEPLUS!");
                msg.setText("¡Hola " + userName + "!\n\n" +
                        "Bienvenido a CINEPLUS, tu experiencia cinematográfica premium.\n\n" +
                        "Ahora puedes:\n" +
                        "- Ver la cartelera actualizada\n" +
                        "- Comprar entradas online\n" +
                        "- Acumular puntos de fidelización\n" +
                        "- Reservar tus asientos favoritos\n\n" +
                        "¡Disfruta del mejor cine!\n\n" +
                        "CINEPLUS");
                mailSender.send(msg);
                log.info("[EMAIL] Correo simple de bienvenida enviado a {}", userEmail);
            }
        } catch (Exception e) {
            log.warn("[EMAIL] No se pudo enviar correo de bienvenida: {} - {}", e.getClass().getSimpleName(), e.getMessage());
        }
    }
    
    private String buildPasswordResetHtml(String userName, String resetLink) {
        return "<!DOCTYPE html>\n" +
            "<html>\n" +
            "<head>\n" +
            "  <meta charset=\"UTF-8\">\n" +
            "  <style>\n" +
            "    body { font-family: Arial, sans-serif; background-color: #f5f5f5; }\n" +
            "    .container { max-width: 600px; margin: 20px auto; background: white; padding: 30px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }\n" +
            "    .header { background: linear-gradient(135deg, #1a1a1a 0%, #2d2d2d 100%); color: white; padding: 20px; border-radius: 4px; text-align: center; }\n" +
            "    .header h1 { margin: 0; font-size: 28px; }\n" +
            "    .content { padding: 20px 0; }\n" +
            "    .btn { display: inline-block; background: #ff6b35; color: white; padding: 12px 30px; text-decoration: none; border-radius: 4px; font-weight: bold; margin: 20px 0; }\n" +
            "    .warning { background: #fff3cd; border-left: 4px solid #ffc107; padding: 12px; margin: 15px 0; color: #856404; }\n" +
            "    .footer { text-align: center; color: #999; font-size: 12px; margin-top: 20px; border-top: 1px solid #eee; padding-top: 15px; }\n" +
            "  </style>\n" +
            "</head>\n" +
            "<body>\n" +
            "  <div class=\"container\">\n" +
            "    <div class=\"header\">\n" +
            "      <h1>🔒 CINEPLUS</h1>\n" +
            "    </div>\n" +
            "    <div class=\"content\">\n" +
            "      <h2>Hola " + userName + ",</h2>\n" +
            "      <p>Has solicitado restablecer tu contraseña de CINEPLUS.</p>\n" +
            "      <p>Haz clic en el botón de abajo para crear una nueva contraseña:</p>\n" +
            "      <div style=\"text-align: center;\">\n" +
            "        <a href=\"" + resetLink + "\" class=\"btn\">Restablecer Contraseña</a>\n" +
            "      </div>\n" +
            "      <div class=\"warning\">\n" +
            "        <strong>⚠️ Importante:</strong> Este enlace expira en 1 hora por seguridad.\n" +
            "      </div>\n" +
            "      <p>Si no solicitaste este cambio, puedes ignorar este mensaje de forma segura.</p>\n" +
            "    </div>\n" +
            "    <div class=\"footer\">\n" +
            "      <p>&copy; 2024 CINEPLUS - Todos los derechos reservados</p>\n" +
            "    </div>\n" +
            "  </div>\n" +
            "</body>\n" +
            "</html>";
    }
    
    private String buildWelcomeHtml(String userName) {
        return "<!DOCTYPE html>\n" +
            "<html>\n" +
            "<head>\n" +
            "  <meta charset=\"UTF-8\">\n" +
            "  <style>\n" +
            "    body { font-family: Arial, sans-serif; background-color: #f5f5f5; }\n" +
            "    .container { max-width: 600px; margin: 20px auto; background: white; padding: 30px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }\n" +
            "    .header { background: linear-gradient(135deg, #ff6b35 0%, #f7931e 100%); color: white; padding: 30px; border-radius: 4px; text-align: center; }\n" +
            "    .header h1 { margin: 0; font-size: 32px; }\n" +
            "    .content { padding: 20px 0; }\n" +
            "    .benefits { background: #f9f9f9; padding: 20px; border-radius: 4px; margin: 20px 0; }\n" +
            "    .benefit-item { display: flex; align-items: center; margin: 10px 0; }\n" +
            "    .benefit-icon { font-size: 24px; margin-right: 15px; }\n" +
            "    .footer { text-align: center; color: #999; font-size: 12px; margin-top: 20px; border-top: 1px solid #eee; padding-top: 15px; }\n" +
            "  </style>\n" +
            "</head>\n" +
            "<body>\n" +
            "  <div class=\"container\">\n" +
            "    <div class=\"header\">\n" +
            "      <h1>🎬 ¡Bienvenido a CINEPLUS!</h1>\n" +
            "    </div>\n" +
            "    <div class=\"content\">\n" +
            "      <h2>¡Hola " + userName + "!</h2>\n" +
            "      <p>Estamos emocionados de tenerte con nosotros. Tu cuenta ha sido creada exitosamente.</p>\n" +
            "      <div class=\"benefits\">\n" +
            "        <h3>¿Qué puedes hacer ahora?</h3>\n" +
            "        <div class=\"benefit-item\">\n" +
            "          <span class=\"benefit-icon\">🎟️</span>\n" +
            "          <span>Compra entradas para las últimas películas</span>\n" +
            "        </div>\n" +
            "        <div class=\"benefit-item\">\n" +
            "          <span class=\"benefit-icon\">⭐</span>\n" +
            "          <span>Acumula puntos de fidelización con cada compra</span>\n" +
            "        </div>\n" +
            "        <div class=\"benefit-item\">\n" +
            "          <span class=\"benefit-icon\">💺</span>\n" +
            "          <span>Reserva tus asientos favoritos con anticipación</span>\n" +
            "        </div>\n" +
            "        <div class=\"benefit-item\">\n" +
            "          <span class=\"benefit-icon\">🍿</span>\n" +
            "          <span>Pre-ordena combos de dulcería</span>\n" +
            "        </div>\n" +
            "      </div>\n" +
            "      <p style=\"text-align: center; margin-top: 30px;\">\n" +
            "        <strong>¡Disfruta del mejor cine!</strong>\n" +
            "      </p>\n" +
            "    </div>\n" +
            "    <div class=\"footer\">\n" +
            "      <p>&copy; 2024 CINEPLUS - Tu experiencia cinematográfica premium</p>\n" +
            "    </div>\n" +
            "  </div>\n" +
            "</body>\n" +
            "</html>";
    }
}
