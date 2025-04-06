package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.example.dto.*;
import org.example.service.KeycloakService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
@Tag(name = "Пользовательский контроллер", description = "Авторизация в приложении")
public class AuthController {
    private KeycloakService keycloakService;
    @PostMapping("/login")
    @Operation(summary = "Авторизация", description = "Авторизация при помощи keycloak")
    public ResponseEntity<AccessTokenResponse> login(@RequestBody @Parameter(required = true) LoginRequest request){
        return ResponseEntity.ok(keycloakService.authenticate(request));
    }
    @PostMapping("/refresh")
    @Operation(summary = "Рефреш токена", description = "Обновление токена используя refresh token")
    public ResponseEntity<AccessTokenResponse> refresh(@RequestBody @Parameter(required = true) RefreshRequest request) {
            return ResponseEntity.ok(keycloakService.refreshToken(request.getRefreshToken()));
    }
    @PostMapping("/register")
    @Operation(summary = "Регистрация", description = "Регистрация нового пользователя в keycloak")
    public ResponseEntity<String> register(@RequestBody @Parameter(required = true) RegistrationRequest request) {
        return keycloakService.registerUser(request);
    }
    @PostMapping("/logout")
    @Operation(summary = "Выход из сессии", description = "Удаление сессии")
    public ResponseEntity<LogoutResponse> logout(
            @RequestParam @Parameter(description = "Токен обновления", required = true) String refreshToken
    ) {
        return keycloakService.logout(refreshToken);
    }
}