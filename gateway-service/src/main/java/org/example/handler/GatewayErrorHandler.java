package org.example.handler;

import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
@Order(-2)
public class GatewayErrorHandler extends AbstractErrorWebExceptionHandler {

    public GatewayErrorHandler(ErrorAttributes errorAttributes,
                               ApplicationContext applicationContext,
                               ServerCodecConfigurer configurer) {
        super(errorAttributes, new WebProperties.Resources(), applicationContext);
        this.setMessageWriters(configurer.getWriters());
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    private Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
        Throwable error = getError(request);
        HttpStatus status = determineHttpStatus(error);

        Map<String, Object> response = Map.of(
                "status", status.value(),
                "error", status.getReasonPhrase(),
                "message", getErrorMessage(error),
                "timestamp", java.time.Instant.now()
        );

        return ServerResponse.status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(response));
    }

    private HttpStatus determineHttpStatus(Throwable error) {
        if (error instanceof NotFoundException) {
            return HttpStatus.NOT_FOUND;
        }
        return HttpStatus.SERVICE_UNAVAILABLE;
    }

    private String getErrorMessage(Throwable error) {
        if (error instanceof NotFoundException) {
            return "Requested route does not exist";
        }
        return "Service is currently unavailable";
    }
}