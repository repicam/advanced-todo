package es.myprojects.notification_email_service.services;

import es.myprojects.notification_email_service.dtos.TaskEventDto;
import es.myprojects.notification_email_service.models.Notification;
import es.myprojects.notification_email_service.repositories.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationHandlerService {

    private final NotificationRepository notificationRepository;
    private final EmailService emailService;

    public void handleTaskNotification(String eventType, TaskEventDto taskEvent) {
        log.info("Processing task event: Type={}, TaskId={}, Title={}",
                eventType, taskEvent.getId(), Objects.requireNonNullElse(taskEvent.getTitle(), ""));

        try {
            Notification notification = Notification.builder()
                    .id("notification-" + System.currentTimeMillis())
                    .type(eventType)
                    .taskTitle(taskEvent.getTitle())
                    .taskId(taskEvent.getId())
                    .message(String.format("Task %s. (ID: %d)", eventType, taskEvent.getId()))
                    .timestamp(LocalDateTime.now())
                    .userEmail(taskEvent.getUserEmail())
                    .build();

            notificationRepository.save(notification);
            log.info("Notification saved successfully for task ID: {}", taskEvent.getId());
        } catch (Exception e) {
            errorHandler(eventType, taskEvent, "Failed to save notification: " + e.getMessage());
        }

        if (eventType.equals("completed")) {
            String subject = "Task completed!";
            String htmlContent = String.format("<p>Hello,</p>" +
                            "<p>Your task <strong>'%s'</strong> has been completed.</p>" +
                            "<p>Details: %s</p>" +
                            "<p>Regards,<br>Your ToDo App Team</p>",
                    taskEvent.getTitle(), taskEvent.getDescription());
            boolean emailSent = emailService.sendEmail(taskEvent.getUserEmail(), subject, htmlContent);
            if (emailSent) {
                log.info("Email notification sent to {} for task ID: {}", taskEvent.getUserEmail(), taskEvent.getId());
            } else {
                errorHandler(eventType, taskEvent, "Failed to send email notification");
            }
        }
    }

    private void errorHandler(String eventType, TaskEventDto taskEvent, String errorMsg) {
        log.warn("An error occurred on task event: Type={}, TaskId={}. Error: {}",
                eventType, taskEvent.getId(), errorMsg);

        //TODO - Guardar la notificación en una tabla de errores
        log.info("Error caught and saved");
    }
}
