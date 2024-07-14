package com.magnasha.powerjolt.service;


import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class JoltAuthorizationManager implements ReactiveAuthorizationManager<AuthorizationContext> {

    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> authentication, AuthorizationContext context) {
        return authentication
                .map(auth -> {
                    boolean isAuthenticated = auth.isAuthenticated();
                    boolean hasUserRole = auth.getAuthorities().stream()
                            .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_USER"));
                    boolean isAuthorized = isAuthenticated && hasUserRole;
                    return new AuthorizationDecision(isAuthorized);
                });
    }
}
