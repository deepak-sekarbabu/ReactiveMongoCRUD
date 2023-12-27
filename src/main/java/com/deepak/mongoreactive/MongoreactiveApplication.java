package com.deepak.mongoreactive;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.EnableWebFlux;

@SpringBootApplication
@EnableWebFlux
public class MongoreactiveApplication {
    public static void main(String[] args) {
        SpringApplication.run(MongoreactiveApplication.class, args);
    }

    @Configuration
    @OpenAPIDefinition(info = @Info(title = "User & Appointment Registration", description = "User & Appointment Registration apis"))
    public static class OpenAPIConfig {
    }

}