package com.app.expensetracker.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestClient;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Optional;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class OpenAPIConfig {

    private final Environment environment;

    @Value("${server.port:8080}")
    private int serverPort;

    @Value("${openapi.output.file}")
    private String outputFileName;

    private String schemeName = "bearerAuth";

    private String bearerFormat = "JWT";

    private String scheme = "bearer";

    private static final String SERVER_SSL_KEY_STORE = "server.ssl.key-store";
    private static final String SERVER_SERVLET_CONTEXT_PATH = "server.servlet.context-path";


    @Bean
    public OpenAPI customConfig() {
        String documentationVersion = environment.getProperty("springdoc.version", "1.0");
        String appTitle = environment.getProperty("springdoc.title", "API Documentation");


        String[] activeProfiles = environment.getActiveProfiles();
        String profileInfo = activeProfiles.length > 0
                ? String.join(", ", activeProfiles).toUpperCase()
                : "DEFAULT";

        String description = String.format("Active profile: %s", profileInfo);

        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList(schemeName))
                .components(new Components()
                        .addSecuritySchemes(schemeName, new SecurityScheme().name(schemeName).type(SecurityScheme.Type.HTTP).bearerFormat(bearerFormat).in(SecurityScheme.In.HEADER).scheme(scheme)))
                .info(new Info()
                        .title(appTitle)
                        .version(documentationVersion)
                        .description(description));
    }

    @Bean
    public CommandLineRunner generateOpenApiJson() {
        return args -> {
            String protocol = Optional.ofNullable(environment.getProperty(SERVER_SSL_KEY_STORE)).map(key -> "https").orElse("http");
            String host = getServerIP();
            String contextPath = Optional.ofNullable(environment.getProperty(SERVER_SERVLET_CONTEXT_PATH)).orElse("");

            // Define the API docs URL
            String apiDocsUrl = String.format("%s://%s:%d%s/v3/api-docs", protocol, host, serverPort, contextPath);

            log.info("Attempting to fetch OpenAPI docs from URL: {}", apiDocsUrl);

            try {
                // Create RestClient instance
                RestClient restClient = RestClient.create();

                // Fetch the OpenAPI JSON
                String response = restClient.get()
                        .uri(apiDocsUrl)
                        .retrieve()
                        .body(String.class);

                // Format and save the JSON to a file
                formatAndSaveToFile(response, outputFileName);

                log.info("OpenAPI documentation generated successfully at {}", outputFileName);

            } catch (Exception e) {
                log.error("Failed to generate OpenAPI documentation from URL: {}", apiDocsUrl, e);
            }
        };
    }

    private String getServerIP() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.error("Error resolving host address", e);
            return "unknown";
        }
    }

    private void formatAndSaveToFile(String content, String fileName) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();

            // Enable pretty-print
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

            // Read the JSON content as a JsonNode
            JsonNode jsonNode = objectMapper.readTree(content);

            // Write the formatted JSON to a file
            objectMapper.writeValue(new File(fileName), jsonNode);

        } catch (IOException e) {
            log.error("Error while saving JSON to file", e);
        }
    }
}
