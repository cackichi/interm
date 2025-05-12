package org.example.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Schema(description = "объект для регистрации")
public class RegistrationRequest {
    @Schema(description = "имя пользователя")
    private String username;
    @Schema(description = "почта")
    private String email;
    @Schema(description = "имя")
    private String firstName;
    @Schema(description = "фамилия")
    private String lastName;
    @Schema(description = "пароль")
    private String password;
}
