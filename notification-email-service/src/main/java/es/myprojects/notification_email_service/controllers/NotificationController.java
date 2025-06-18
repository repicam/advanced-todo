package es.myprojects.notification_email_service.controllers;

import es.myprojects.notification_email_service.models.Notification;
import es.myprojects.notification_email_service.repositories.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationRepository notificationRepository;

    @GetMapping
    public ResponseEntity<List<Notification>> getAllNotifications() {
        List<Notification> notifications = StreamSupport.stream(notificationRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
        return ResponseEntity.ok(notifications);
    }
}
