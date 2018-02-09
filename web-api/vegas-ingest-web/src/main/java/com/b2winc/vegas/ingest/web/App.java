package com.b2winc.vegas.ingest.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = "com.b2winc")
@EnableJpaRepositories("com.b2winc.vegas.ingest.repository")
@EntityScan(basePackages = {"com.b2winc.vegas.ingest.model.domain", "com.b2winc.vegas.ingest.dto"})
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
