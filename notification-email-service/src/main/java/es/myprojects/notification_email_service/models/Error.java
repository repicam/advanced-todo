package es.myprojects.notification_email_service.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@RedisHash("Error")
public class Error {

    @Id
    private String id;
    private String eventType;
    private Long taskId;
    private LocalDateTime timestamp;
    private String errorMessage;
}
