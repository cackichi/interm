package org.example.security;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Objects;

@AllArgsConstructor
@Slf4j
public class KeycloakService {
    private final Environment environment;
    private final RestTemplate restTemplate;

    public boolean validateToken(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("token", accessToken);
        parameters.add("client_id", environment.getProperty("keycloak.resource"));
        parameters.add("client_secret", environment.getProperty("keycloak.credentials.secret"));

        log.info("Send request for check token from kafka");
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(parameters, headers);
        ResponseEntity<IntrospectResponse> response = restTemplate.postForEntity(
                getIntrospectUrl(),
                request,
                IntrospectResponse.class
        );
        log.info("Result of request - {}", Objects.requireNonNull(response.getBody()).isActive());
        return response.getBody() != null && response.getBody().isActive();
    }

    private String getIntrospectUrl() {
        return UriComponentsBuilder.fromHttpUrl(environment.getProperty("keycloak.auth-server-url"))
                .pathSegment("realms")
                .pathSegment(environment.getProperty("keycloak.realm"))
                .pathSegment("protocol")
                .pathSegment("openid-connect")
                .pathSegment("token")
                .pathSegment("introspect")
                .toUriString();
    }
}
