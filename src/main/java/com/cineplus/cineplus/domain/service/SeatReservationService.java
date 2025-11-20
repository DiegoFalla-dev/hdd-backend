package com.cineplus.cineplus.domain.service;

import java.util.List;
import java.util.Set;

public interface SeatReservationService {
    
    /**
     * Inicia una nueva sesión de reserva de asientos
     * @param showtimeId ID del showtime
     * @param seatIdentifiers Identificadores de los asientos a reservar
     * @param userId ID del usuario (opcional)
     * @return sessionId generado para la reserva
     */
    String initiateSeatReservation(Long showtimeId, Set<String> seatIdentifiers, Long userId);
    
    /**
     * Libera los asientos de una sesión específica
     * @param sessionId ID de la sesión
     */
    void releaseReservationBySession(String sessionId);
    
    /**
     * Confirma la reserva y convierte los asientos en OCCUPIED
     * @param sessionId ID de la sesión
     * @param purchaseNumber Número de orden/compra
     */
    void confirmReservation(String sessionId, String purchaseNumber);
    
    /**
     * Cancela permanentemente un grupo de asientos por coordenadas
     * @param showtimeId ID del showtime
     * @param seatIdentifiers Identificadores de los asientos a cancelar
     * @param purchaseNumber Número de orden asociado a la cancelación
     */
    void cancelSeatsPermanently(Long showtimeId, Set<String> seatIdentifiers, String purchaseNumber);
    
    /**
     * Libera asientos OCCUPIED a AVAILABLE (solo si no están permanentemente cancelados)
     * @param showtimeId ID del showtime
     * @param seatIdentifiers Identificadores de los asientos a liberar
     */
    void releaseOccupiedSeats(Long showtimeId, Set<String> seatIdentifiers);
    
    /**
     * Obtiene los asientos reservados por una sesión
     * @param sessionId ID de la sesión
     * @return Lista de identificadores de asientos
     */
    List<String> getReservedSeatsBySession(String sessionId);
    
    /**
     * Libera todas las reservas expiradas automáticamente (llamado por el scheduler)
     */
    void releaseExpiredReservations();
}
