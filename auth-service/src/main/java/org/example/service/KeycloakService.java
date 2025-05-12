package org.example.service;

import lombok.AllArgsConstructor;
import org.example.dto.AccessTokenResponse;
import org.example.dto.LoginRequest;
import org.example.dto.LogoutResponse;
import org.example.dto.RegistrationRequest;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@AllArgsConstructor
public class KeycloakService {
    private final Environment environment;
    private final RestTemplate restTemplate;
    private final KeycloakUtil keycloakUtil;

    public AccessTokenResponse authenticate(LoginRequest request) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
            parameters.add("username", request.getUsername());
            parameters.add("password", request.getPassword());
            parameters.add("grant_type", "password");
            parameters.add("client_id", environment.getProperty("keycloak.resource"));
            parameters.add("client_secret", environment.getProperty("keycloak.credentials.secret"));

            ResponseEntity<AccessTokenResponse> response = restTemplate.exchange(
                    keycloakUtil.getAuthUrl(),
                    HttpMethod.POST,
                    new HttpEntity<>(parameters, headers),
                    AccessTokenResponse.class
            );
            return response.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public AccessTokenResponse refreshToken(String refreshToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("grant_type", "refresh_token");
        parameters.add("refresh_token", refreshToken);
        parameters.add("client_id", environment.getProperty("keycloak.resource"));
        parameters.add("client_secret", environment.getProperty("keycloak.credentials.secret"));

        AccessTokenResponse response = restTemplate.exchange(
                keycloakUtil.getAuthUrl(),
                HttpMethod.POST,
                new HttpEntity<>(parameters, headers),
                AccessTokenResponse.class
        ).getBody();

        return response;
    }

    public ResponseEntity<String> registerUser(RegistrationRequest registrationRequest) {
        try {
            String adminToken = keycloakUtil.getAdminAccessToken();

            keycloakUtil.createUserWithPassword(adminToken, registrationRequest);

            return ResponseEntity.ok("User registered successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Registration failed: " + e.getMessage());
        }
    }

    public ResponseEntity<LogoutResponse> logout(String refreshToken) {
        try {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

                MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
                params.add("client_id", environment.getProperty("keycloak.resource"));
                params.add("client_secret", environment.getProperty("keycloak.credentials.secret"));
                params.add("refresh_token", refreshToken);

                restTemplate.exchange(
                        keycloakUtil.getLogoutUrl(),
                        HttpMethod.POST,
                        new HttpEntity<>(params, headers),
                        Void.class
                );
            return ResponseEntity.ok(new LogoutResponse("Logged out successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new LogoutResponse("Logout failed: " + e.getMessage()));
        }
    }
}
