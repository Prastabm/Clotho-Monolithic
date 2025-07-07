package com.clotho.monolithic.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    // Completely disables default security and in-memory login
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        System.out.println("✅ Custom SecurityConfig loaded — all routes are open");

        return http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                )
                .httpBasic(Customizer.withDefaults()) // Required to prevent auto-login form
                .formLogin(form -> form.disable())    // Disable login page
                .build();
    }

    // Prevent auto-configured user details service from kicking in
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> null;
    }
}
