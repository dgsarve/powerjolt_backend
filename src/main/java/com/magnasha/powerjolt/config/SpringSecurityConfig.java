package com.magnasha.powerjolt.config;


import com.magnasha.powerjolt.service.JWTAuthenticationManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.session.InMemoryReactiveSessionRegistry;
import org.springframework.security.core.session.ReactiveSessionRegistry;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.SessionLimit;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.List;


@Slf4j
@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity

public class SpringSecurityConfig {


    private final JWTAuthenticationManager jwtAuthenticationManager;

    public SpringSecurityConfig(JWTAuthenticationManager jwtAuthenticationManager) {
        this.jwtAuthenticationManager = jwtAuthenticationManager;
    }


    @Bean
    SecurityWebFilterChain apiHttpSecurity(ServerHttpSecurity http) {
        http.authorizeExchange(
                authorizeRequests -> authorizeRequests.pathMatchers("/", "/home", "/api/login", "/api/auth/google", "/api/transform").permitAll()
                        .anyExchange().authenticated());
        http.sessionManagement((sessions) -> sessions
                .concurrentSessions((concurrency) -> concurrency
                        .maximumSessions(SessionLimit.UNLIMITED))
        );
        http.authenticationManager(jwtAuthenticationManager);
        http.csrf(csrf -> csrf.disable());
        return http.build();
    }


    @Bean
    ReactiveSessionRegistry reactiveSessionRegistry() {
        return new InMemoryReactiveSessionRegistry();
    }

    @Bean
    CorsWebFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(List.of("http://localhost", "http://localhost:3000","http://localhost:3001"));
        config.addAllowedHeader("*");
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        log.debug("Allowed Origins: " + config.getAllowedOrigins());
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsWebFilter(source);
    }

}
