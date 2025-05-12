package org.example.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Schema(description = "Ответ с информацией авторизации")
public class AccessTokenResponse {
    @JsonProperty("access_token")
    @Schema(description = "access токен")
    private String accessToken;

    @JsonProperty("expires_in")
    @Schema(description = "время жизни access токена")
    private int expiresIn;

    @JsonProperty("refresh_expires_in")
    @Schema(description = "время жизни refresh токена")
    private int refreshExpiresIn;

    @JsonProperty("refresh_token")
    @Schema(description = "refresh токен")
    private String refreshToken;

    @JsonProperty("token_type")
    @Schema(description = "тип токена")
    private String tokenType;

    @JsonProperty("not-before-policy")
    private int notBeforePolicy;

    @JsonProperty("session_state")
    private String sessionState;

    @JsonProperty("scope")
    private String scope;
}
