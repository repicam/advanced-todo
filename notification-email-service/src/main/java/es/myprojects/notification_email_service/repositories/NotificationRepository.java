package es.myprojects.notification_email_service.repositories;

import es.myprojects.notification_email_service.models.Notification;
import org.springframework.data.repository.CrudRepository;

public interface NotificationRepository extends CrudRepository<Notification, String> {
}
