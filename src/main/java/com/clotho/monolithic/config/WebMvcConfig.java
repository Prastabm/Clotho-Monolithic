package com.clotho.monolithic.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * This class provides global CORS (Cross-Origin Resource Sharing) configuration
 * for the entire Spring Boot application.
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer{

    /**
     * This method configures the CORS settings. It allows requests from specified
     * origins and defines which HTTP methods and headers are permitted.
     *
     * @param registry The CorsRegistry to which the CORS configuration is added.
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Apply this configuration to all endpoints in the application.

                // Add the URL of your frontend application here.
                // For development, this is typically http://localhost:3000 or http://localhost:5173
                .allowedOrigins("http://localhost:3000", "http://localhost:5173") // Add more origins if needed

                // You can use allowedOriginPatterns("*") for development, but it's less secure.
                // .allowedOriginPatterns("*")

                // Specify which HTTP methods are allowed.
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")

                // Allow all headers in the request.
                .allowedHeaders("*")

                // Allow credentials (like cookies, authorization headers) to be sent.
                .allowCredentials(true);
    }
}
