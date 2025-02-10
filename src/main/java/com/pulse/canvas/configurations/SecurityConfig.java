package com.pulse.canvas.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                // Allow public access to authentication and Swagger-related endpoints
                .requestMatchers("/auth/authenticate", "/auth/register", "/auth/refresh-token", "/v2/api-docs",
                        "/swagger-resources/**", "/swagger-ui.html", "/webjars/**", "/v3/api-docs/**",
                        "/swagger-ui/**", "/auth/emails").permitAll()

                // Secure authentication-related endpoints for users with role ADMIN_METIER
                .requestMatchers("/auth/**").hasRole("ADMIN_METIER")

                // Allow all users (authenticated or not) to access /canvas WebSocket
                .requestMatchers("/canvas/**").permitAll()  // Fix: Permit all access to WebSocket endpoint

                .and()
                .httpBasic()  // Use basic authentication for HTTP basic security

                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)  // Stateless session configuration (for WebSockets)

                .and()
                .csrf().disable();  // Disable CSRF for WebSocket support

        return http.build();
    }
}
