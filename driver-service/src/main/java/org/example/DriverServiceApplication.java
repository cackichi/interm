package org.example;

import io.mongock.runner.springboot.EnableMongock;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableMongock
@SpringBootApplication
@EnableDiscoveryClient
@OpenAPIDefinition(info = @Info(
        title = "Driver Service API",
        description = "API сервиса водителей",
        version = "1.0.0"
))
public class DriverServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(DriverServiceApplication.class, args);
    }

}