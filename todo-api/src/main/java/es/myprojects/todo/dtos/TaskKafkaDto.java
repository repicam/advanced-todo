package es.myprojects.todo.dtos;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@SuperBuilder
public class TaskKafkaDto extends TaskDto {

    private String userEmail;

    public static TaskKafkaDto fromTaskDto(TaskDto dto, String userEmail) {
        return TaskKafkaDto.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .description(dto.getDescription())
                .completed(dto.isCompleted())
                .userEmail(userEmail)
                .build();
    }
}
