package es.myprojects.todo.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class TaskDto {

    private Long id;
    @NotBlank(message = "Title is required")
    private String title;
    private String description;
    private boolean completed;
}
