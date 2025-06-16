package es.myprojects.notification_email_service.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskEventDto {

    private Long id;
    private String title;
    private String description;
    private boolean completed;
}
