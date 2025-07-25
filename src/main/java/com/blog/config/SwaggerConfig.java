package com.blog.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Swagger/OpenAPI 설정
 * 
 * @author Minseong Kang, devKobe24
 * @version 1.0
 */
@Configuration
public class SwaggerConfig {
    
    @Value("${spring.profiles.active:prod}")
    private String activeProfile;
    
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
            .info(apiInfo())
            .servers(servers())
            .components(components())
            .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"));
    }
    
    private Info apiInfo() {
        return new Info()
            .title("Blog API")
            .description("블로그 백엔드 API 문서")
            .version("1.0.0")
            .contact(new Contact()
                .name("Minseong Kang")
                .email("devkobe24@gmail.com")
                .url("https://github.com/devKobe24"))
            .license(new License()
                .name("MIT License")
                .url("https://opensource.org/licenses/MIT"));
    }
    
    private List<Server> servers() {
        Server localServer = new Server()
            .url("http://localhost:8080")
            .description("Local Development Server");
            
        Server prodServer = new Server()
            .url("https://api.blog.com")
            .description("Production Server");
        
        return List.of(localServer, prodServer);
    }
    
    private Components components() {
        return new Components()
            .addSecuritySchemes("Bearer Authentication", createAPIKeyScheme());
    }
    
    private SecurityScheme createAPIKeyScheme() {
        return new SecurityScheme()
            .type(SecurityScheme.Type.HTTP)
            .bearerFormat("JWT")
            .scheme("bearer")
            .description("JWT 토큰을 입력하세요. Bearer 접두사는 자동으로 추가됩니다.");
    }
} 