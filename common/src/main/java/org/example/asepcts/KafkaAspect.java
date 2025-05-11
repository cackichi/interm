package org.example.asepcts;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.example.exception.UnauthorizedException;
import org.example.security.KeycloakService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

@Aspect
@AllArgsConstructor
@Slf4j
public class KafkaAspect {
    private KeycloakService keycloakService;
    @Pointcut("@annotation(KafkaHandler) && args(record)")
    public void callKafkaHandler(ConsumerRecord<?, ?> record) {}

    @Around("callKafkaHandler(record)")
    public Object validateKafkaToken(ProceedingJoinPoint joinPoint, ConsumerRecord<?, ?> record) throws Throwable {
        String token = extractTokenFromHeaders(record);
        log.info("Token found - {}", token);
        if (!keycloakService.validateToken(token)) {
            log.warn("Token invalid - {}", token);
            throw new UnauthorizedException("Invalid access token");
        } else {
            log.info("Token valid - {}", token);
            Jwt jwt = Jwt.withTokenValue(token)
                    .header("alg", "RS256")
                    .claim("sub", "service-account")
                    .build();
            SecurityContextHolder.getContext().setAuthentication(
                    new JwtAuthenticationToken(jwt)
            );
        }

        return joinPoint.proceed();
    }

    private String extractTokenFromHeaders(ConsumerRecord<?, ?> record) throws UnauthorizedException {
        Iterable<Header> headers = record.headers().headers("X-Access-Token");
        if (headers.iterator().hasNext()) {
            Header tokenHeader = headers.iterator().next();
            return new String(tokenHeader.value());
        }
        log.error("Token not found in kafka headers");
        throw new UnauthorizedException("Access token not found in message headers");
    }
}
