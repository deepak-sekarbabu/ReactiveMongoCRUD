package com.deepak.mongoreactive;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.reactive.config.EnableWebFlux;

@SpringBootApplication
@EnableWebFlux
public class MongoreactiveApplication {

    public static void main(String[] args) {
        SpringApplication.run(MongoreactiveApplication.class, args);
    }

}