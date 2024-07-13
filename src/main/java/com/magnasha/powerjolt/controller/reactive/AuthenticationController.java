package com.magnasha.powerjolt.controller.reactive;

/**
 * @Author Gnana Prakasam Duraisamy
 * @Date 04.07.24 14:46
 * @Version 1.0
 */


import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.magnasha.powerjolt.service.UserService;
import com.magnasha.powerjolt.utils.JwtUtil;
import com.magnasha.powerjolt.wsdto.AuthResponse;
import com.magnasha.powerjolt.wsdto.TokenRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Collections;

@Slf4j
@RestController
@RequestMapping("/api")
public class AuthenticationController {
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;


    @Value("${google.clientId}")
    private String googleClientId;

    @PostMapping("/auth/google")
    public Mono<? extends ResponseEntity<AuthResponse>> login(@RequestBody TokenRequest tokenRequest) {
        return Mono.fromCallable(() -> {
                    GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                            GoogleNetHttpTransport.newTrustedTransport(), GsonFactory.getDefaultInstance())
                            .setAudience(Collections.singletonList(googleClientId))
                            .build();

                    GoogleIdToken idToken = verifier.verify(tokenRequest.getToken());
                    if (idToken != null) {
                        return idToken.getPayload();
                    } else {
                        throw new IllegalArgumentException("Invalid ID token.");
                    }
                })
                .flatMap(payload -> {
                    String userName = payload.getEmail();
                    return userService.findOrCreateUser(payload)
                            .map(user -> {
                                String jwtToken = jwtUtil.generateToken(userName);
                                log.debug("jwtToken :: {} ",jwtToken);
                                return ResponseEntity.ok(new AuthResponse(jwtToken, "Success"));
                            });
                })
                .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().body(new AuthResponse("", "Bad credentials"))));
    }


    @GetMapping("/login/failure")
    public Mono<String> loginFailure() {
        return Mono.just("Login failed");
    }

    @GetMapping("/user")
    public Mono<OAuth2User> getUser(@AuthenticationPrincipal OAuth2User principal) {
        return Mono.justOrEmpty(principal);
    }
}
