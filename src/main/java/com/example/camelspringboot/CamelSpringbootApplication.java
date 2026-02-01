package com.example.camelspringboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class CamelSpringbootApplication {

    public static void main(String[] args) {
        SpringApplication.run(CamelSpringbootApplication.class, args);
    }

}
