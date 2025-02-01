package com.exportgenerator.demo.config;

import com.exportgenerator.demo.filters.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${auth.enabled}")
    private boolean authEnabled;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable());

        if (authEnabled) {
            http.authorizeHttpRequests(auth -> auth
                    .requestMatchers("/api/public/**").permitAll() // Public endpoints
                    .requestMatchers("/swagger-ui/**").permitAll() // Allow Swagger UI access
                    .requestMatchers("/v3/api-docs/**").permitAll() // Allow Swagger API docs access
                    .requestMatchers("/swagger-resources/**").permitAll() // Allow Swagger resources access
                    .anyRequest().authenticated() // Secure all other endpoints
            )
                    .addFilterBefore(new JwtAuthenticationFilter(), BasicAuthenticationFilter.class)
                    .sessionManagement(session -> session
                            .sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        } else {
            http.authorizeHttpRequests(auth -> auth
                    .anyRequest().permitAll() // Allow all requests without authentication
            );
        }

        return http.build();
    }
}
