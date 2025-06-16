package es.myprojects.notification_email_service.services;

import es.myprojects.notification_email_service.dtos.TaskEventDto;
import es.myprojects.notification_email_service.exceptions.ExternalServiceException;
import es.myprojects.notification_email_service.models.Notification;
import es.myprojects.notification_email_service.repositories.NotificationRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationHandlerService {

    private final NotificationRepository notificationRepository;
    private final Random random = new Random(); // Para simular fallos

    // Aplicamos el Circuit Breaker "externalService" a este método
    @CircuitBreaker(name = "externalService", fallbackMethod = "fallbackNotificationHandler")
    public void handleTaskNotification(String eventType, TaskEventDto taskEvent) {
        log.info("Processing task event: Type={}, TaskId={}, Title={}",
                eventType, taskEvent.getId(), Objects.requireNonNullElse(taskEvent.getTitle(), ""));

        // --- Simulación de fallo de un servicio externo ---
        // Esto provocará que el Circuit Breaker se abra si falla con demasiada frecuencia
        if (random.nextInt(10) < 3) { // 30% de probabilidad de fallo
            log.error("Simulating external service failure for task ID: {}", taskEvent.getId());
            throw new ExternalServiceException("Failed to send notification to external service.");
        }
        // --- Fin de simulación ---

        Notification notification = Notification.builder()
                .id("notification-" + System.currentTimeMillis())
                .type(eventType)
                .taskTitle(taskEvent.getTitle())
                .taskId(taskEvent.getId())
                .message(String.format("Task %sed. (ID: %d)", eventType, taskEvent.getId()))
                .timestamp(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);
        log.info("Notification saved successfully for task ID: {}", taskEvent.getId());
    }

    // Método de fallback para el Circuit Breaker
    private void fallbackNotificationHandler(String eventType, TaskEventDto taskEvent, Throwable throwable) {
        log.warn("Fallback triggered for task event: Type={}, TaskId={}, Title={}. Reason: {}",
                eventType, taskEvent.getId(), Objects.requireNonNullElse(taskEvent.getTitle(), ""), throwable.getMessage());

        // Aquí puedes implementar una lógica de fallback:
        // - Guardar el evento en una cola de reintento (Dead Letter Queue - DLQ)
        // - Enviar una alerta a un sistema de monitorización
        // - Registrar el fallo y omitir la notificación por ahora
        // Para este ejemplo, simplemente registramos que se usó el fallback.

        Notification fallbackNotification = Notification.builder()
                .id("fallback-" + System.currentTimeMillis())
                .type("fallback-" + eventType)
                .taskTitle(taskEvent.getTitle())
                .taskId(taskEvent.getId())
                .message(String.format("Fallback: Could not process task '%s' (%s) due to external service failure. Error: %s",
                        taskEvent.getId(), eventType, throwable.getMessage()))
                .timestamp(LocalDateTime.now())
                .build();
        notificationRepository.save(fallbackNotification);
        log.info("Fallback notification saved for task ID: {}", taskEvent.getId());
    }
}
