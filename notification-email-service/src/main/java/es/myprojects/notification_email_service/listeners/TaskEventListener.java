package es.myprojects.notification_email_service.listeners;

import es.myprojects.notification_email_service.dtos.TaskEventDto;
import es.myprojects.notification_email_service.services.NotificationHandlerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TaskEventListener {

    private final NotificationHandlerService notificationHandlerService;

    @KafkaListener(topics = "task-events", groupId = "notification-group")
    public void listenTaskEvents(@Header(KafkaHeaders.RECEIVED_KEY) String key, TaskEventDto taskEvent) {
        log.info("Received Kafka message - Key: {}, Task ID: {}, Title: {}", key, taskEvent.getId(), taskEvent.getTitle());
        notificationHandlerService.handleTaskNotification(key, taskEvent);
    }
}
