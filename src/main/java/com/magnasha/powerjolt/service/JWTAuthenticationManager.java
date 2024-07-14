package com.magnasha.powerjolt.service;


import com.magnasha.powerjolt.utils.JwtUtil;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class JWTAuthenticationManager implements ReactiveAuthenticationManager {

    private final JwtUtil jwtUtil;
    private final UserService userService;

    public JWTAuthenticationManager(JwtUtil jwtUtil, UserService userService) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }


    @Override
    public Mono<Authentication> authenticate(Authentication authentication) throws AuthenticationException {
        String token = authentication.getCredentials().toString();
        String username = jwtUtil.extractUsername(token);

        return userService.findByUserName(username)
                .map(userDetails -> {
                    if (jwtUtil.validateToken(token, username)) {
                        return authentication;
                    } else {
                        throw new AuthenticationException("Invalid JWT token") {
                        };
                    }
                });
    }


}