package es.myprojects.todo.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskDto {

    private Long id;
    @NotBlank(message = "Title is required")
    private String title;
    private String description;
    private boolean completed;
    private String userEmail;
}
