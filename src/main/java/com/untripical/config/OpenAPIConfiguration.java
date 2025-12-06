package com.untripical.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfiguration {

    @Bean
    public OpenAPI customOpenAPI() {
        Server server = new Server();
        server.setUrl("http://localhost:8080");
        server.setDescription("Untripical server");

        Contact contact = new Contact();
        contact.setName("Mateusz");
        contact.setEmail("mateusz@untripical.com");

        Info info = new Info()
                .title("Untripical API")
                .version("1.0")
                .description("API aplication Untripical")
                .contact(contact);

        return new OpenAPI()
                .addServersItem(server)
                .info(info);
    }
}
