package com.gravitlauncher.simplecabinet.web.configuration;

import com.gravitlauncher.simplecabinet.web.WebApplication;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("SimpleCabinet 2 Web API")
                        .version(WebApplication.VERSION)
                        .description("Swagger documentation for the SimpleCabinet 2 backend. "
                                + "Endpoints are grouped by module and secured routes use JWT bearer authentication.")
                        .contact(new Contact().name("SimpleCabinet")))
                .components(new Components()
                        .addSchemas("ApiError", new ObjectSchema()
                                .addProperty("code", new IntegerSchema().description("Application-specific error code"))
                                .addProperty("error", new StringSchema().description("Human-readable error message")))
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .name("bearerAuth")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("public")
                .pathsToMatch(
                        "/auth/**",
                        "/users/**",
                        "/news/**",
                        "/status/**",
                        "/servers/**",
                        "/profile/**",
                        "/group/**",
                        "/banlist/**",
                        "/exchangerate/**",
                        "/reputation/**",
                        "/setup",
                        "/myip")
                .build();
    }

    @Bean
    public GroupedOpenApi cabinetApi() {
        return GroupedOpenApi.builder()
                .group("cabinet")
                .pathsToMatch("/cabinet/**")
                .build();
    }

    @Bean
    public GroupedOpenApi adminApi() {
        return GroupedOpenApi.builder()
                .group("admin")
                .pathsToMatch("/admin/**")
                .build();
    }

    @Bean
    public GroupedOpenApi launcherApi() {
        return GroupedOpenApi.builder()
                .group("launcher")
                .pathsToMatch("/launcher/**")
                .build();
    }

    @Bean
    public GroupedOpenApi shopApi() {
        return GroupedOpenApi.builder()
                .group("shop")
                .pathsToMatch("/shop/**")
                .build();
    }

    @Bean
    public GroupedOpenApi integrationsApi() {
        return GroupedOpenApi.builder()
                .group("integrations")
                .pathsToMatch("/integration/**", "/webhooks/**")
                .build();
    }
}
