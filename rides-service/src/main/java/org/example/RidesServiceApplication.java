package org.example;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
@OpenAPIDefinition(info = @Info(
        title = "Rides Service API",
        description = "API сервиса поездок",
        version = "1.0.0"
))
public class RidesServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(RidesServiceApplication.class, args);
    }
}