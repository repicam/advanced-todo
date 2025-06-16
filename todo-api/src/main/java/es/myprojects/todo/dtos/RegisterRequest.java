package es.myprojects.todo.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class RegisterRequest extends LoginRequest{

    @Email(message = "Email should be valid")
    @NotBlank
    private String email;
}
