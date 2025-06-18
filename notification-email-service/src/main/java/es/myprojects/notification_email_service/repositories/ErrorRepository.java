package es.myprojects.notification_email_service.repositories;

import es.myprojects.notification_email_service.models.Error;
import org.springframework.data.repository.CrudRepository;

public interface ErrorRepository extends CrudRepository<Error, String> {
}
