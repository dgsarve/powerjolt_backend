package com.magnasha.powerjolt.config;


import com.magnasha.powerjolt.filter.JwtAuthenticationFilter;
import com.magnasha.powerjolt.service.JWTAuthenticationManager;
import com.magnasha.powerjolt.service.JoltAuthorizationManager;
import com.magnasha.powerjolt.service.UserService;
import com.magnasha.powerjolt.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.session.InMemoryReactiveSessionRegistry;
import org.springframework.security.core.session.ReactiveSessionRegistry;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.SessionLimit;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.List;


@Slf4j
@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity

public class SpringSecurityConfig {
    private final JwtUtil jwtUtil;
    private final UserService userService;

    private final JWTAuthenticationManager jwtAuthenticationManager;
    private final JoltAuthorizationManager joltAuthorizationManager;

    public SpringSecurityConfig(JwtUtil jwtUtil, UserService userService, JWTAuthenticationManager jwtAuthenticationManager, JoltAuthorizationManager joltAuthorizationManager) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.jwtAuthenticationManager = jwtAuthenticationManager;
        this.joltAuthorizationManager = joltAuthorizationManager;
    }


    @Bean
    SecurityWebFilterChain apiHttpSecurity(ServerHttpSecurity http) {
        http.csrf(csrf -> csrf.disable())
        .authorizeExchange(
                authorizeRequests -> authorizeRequests.pathMatchers("/", "/home", "/api/login", "/api/auth/google", "/api/transform", "/api/history")
                        .permitAll()
                        );
        http.authorizeExchange(authorizeRequests ->authorizeRequests.pathMatchers("/api/**").authenticated());
        http.sessionManagement((sessions) -> sessions
                .concurrentSessions((concurrency) -> concurrency
                        .maximumSessions(SessionLimit.UNLIMITED))
        );
        http.securityContextRepository(NoOpServerSecurityContextRepository.getInstance());
        http.authenticationManager(jwtAuthenticationManager);
        http.addFilterAt(new JwtAuthenticationFilter(jwtUtil, userService), SecurityWebFiltersOrder.AUTHENTICATION);


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
        config.setAllowedOrigins(List.of("http://localhost", "http://localhost:3000", "http://localhost:3001"));
        config.addAllowedHeader("*");
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        log.debug("Allowed Origins: " + config.getAllowedOrigins());
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsWebFilter(source);
    }

}
