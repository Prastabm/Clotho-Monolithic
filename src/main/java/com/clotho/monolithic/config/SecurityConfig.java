package com.clotho.monolithic.config;

import com.clotho.monolithic.common.security.FirebaseJwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final FirebaseJwtAuthenticationFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(cors -> cors.configure(http))
                .csrf(csrf -> csrf.disable()) // CSRF is already disabled
                .authorizeHttpRequests(auth -> auth
                        // 🔓 Public endpoints
                        .requestMatchers(HttpMethod.POST, "/auth/signup", "/auth/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/auth/test-api-key").permitAll()
                        .requestMatchers("/api/stripe/webhook").permitAll() // ✅ ALLOW STRIPE WEBHOOKS

                        // 🔐 Auth endpoints
                        .requestMatchers(HttpMethod.GET, "/auth/me").authenticated()

                        // 🛒 Cart access for authenticated users
                        .requestMatchers(HttpMethod.GET, "/api/cart/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/cart/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/cart/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/cart/**").authenticated()

                        // 👤 Users (not admins) can place orders
                        .requestMatchers(HttpMethod.POST, "/api/orders").hasRole("USER")

                        // 👮 Admin-only product & inventory updates
                        .requestMatchers(HttpMethod.POST, "/products/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/products/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/products/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/inventory/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/inventory/**").hasRole("ADMIN")

                        // 📦 Everyone logged in can view products/inventory
                        .requestMatchers(HttpMethod.GET, "/products/**", "/inventory/**").authenticated()

                        // 🌐 Default: all other endpoints require auth
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}