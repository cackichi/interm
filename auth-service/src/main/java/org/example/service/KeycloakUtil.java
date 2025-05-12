package org.example.service;

import lombok.AllArgsConstructor;
import org.example.dto.RegistrationRequest;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Component
public class KeycloakUtil {
    private final RestTemplate restTemplate;
    private final Environment environment;
    public String getAdminAccessToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "password");
        params.add("client_id", "admin-cli");
        params.add("username", environment.getProperty("keycloak.admin-username"));
        params.add("password", environment.getProperty("keycloak.admin-password"));

        String authUrl = UriComponentsBuilder.fromHttpUrl(environment.getProperty("keycloak.auth-server-url"))
                .pathSegment("realms")
                .pathSegment("master")
                .pathSegment("protocol")
                .pathSegment("openid-connect")
                .pathSegment("token")
                .toUriString();

        ResponseEntity<Map> response = restTemplate.exchange(
                authUrl,
                HttpMethod.POST,
                new HttpEntity<>(params, headers),
                Map.class
        );

        return (String) response.getBody().get("access_token");
    }

    public void createUserWithPassword(String adminToken, RegistrationRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(adminToken);

        Map<String, Object> credentials = new HashMap<>();
        credentials.put("type", "password");
        credentials.put("value", request.getPassword());
        credentials.put("temporary", false);

        Map<String, Object> user = new HashMap<>();
        user.put("username", request.getUsername());
        user.put("email", request.getEmail());
        user.put("firstName", request.getFirstName());
        user.put("lastName", request.getLastName());
        user.put("enabled", true);
        user.put("emailVerified", false);
        user.put("credentials", List.of(credentials));

        String usersUrl = UriComponentsBuilder.fromHttpUrl(environment.getProperty("keycloak.auth-server-url"))
                .pathSegment("admin")
                .pathSegment("realms")
                .pathSegment(environment.getProperty("keycloak.realm"))
                .pathSegment("users")
                .toUriString();

        restTemplate.exchange(
                usersUrl,
                HttpMethod.POST,
                new HttpEntity<>(user, headers),
                Void.class
        );
    }

    public String getLogoutUrl() {
        return UriComponentsBuilder.fromHttpUrl(environment.getProperty("keycloak.auth-server-url"))
                .pathSegment("realms")
                .pathSegment(environment.getProperty("keycloak.realm"))
                .pathSegment("protocol")
                .pathSegment("openid-connect")
                .pathSegment("logout")
                .toUriString();
    }

    public String getAuthUrl() {
        return UriComponentsBuilder.fromHttpUrl(environment.getProperty("keycloak.auth-server-url"))
                .pathSegment("realms")
                .pathSegment(environment.getProperty("keycloak.realm"))
                .pathSegment("protocol")
                .pathSegment("openid-connect")
                .pathSegment("token")
                .toUriString();
    }
}
