package com.project.sunauloNepal.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.servers.Server;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerConfig {

	@Bean
	public OpenAPI customOpenAPI() {
	    Server server = new Server();
	    server.setUrl("https://0a09047aad5a.ngrok-free.app"); // exact ngrok URL

	    return new OpenAPI()
	            .info(new Info().title("SuanoloNepal API").version("1.0.0").description("API docs for SunauloNepal"))
	            .addServersItem(server)
	            .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
	            .components(new Components().addSecuritySchemes("Bearer Authentication",
	                new SecurityScheme().type(SecurityScheme.Type.HTTP)
	                                    .scheme("bearer")
	                                    .bearerFormat("JWT")));
	}

}

