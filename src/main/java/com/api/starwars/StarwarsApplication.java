package com.api.starwars;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoAuditing
@EnableFeignClients
@EnableMongoRepositories("com.api.starwars.domain")
@OpenAPIDefinition(info = @Info(
        title = "StarWars API",
        description = "API responsible for gateway between StarWars API and other services",
        version = "v1",
        contact = @Contact(email = "eddieprofessionalmail@gmail.com", name = "Edcleidson de Souza Cardoso Junior")
))
public class StarwarsApplication {

    public static void main(String[] args) {
        SpringApplication.run(StarwarsApplication.class, args);
    }
}
