package com.magnasha.powerjolt.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.magnasha.powerjolt.document.ContactForm;
import com.magnasha.powerjolt.document.User;
import com.magnasha.powerjolt.repository.ContactFormRepository;
import com.magnasha.powerjolt.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContactFormRepository contactFormRepository;

    public Mono<User> findOrCreateUser(GoogleIdToken.Payload payload) {
        User user = new User();
        user.setEmail(payload.getEmail());
        user.setName(String.valueOf(payload.get("name")));
        user.setPicture(String.valueOf(payload.get("picture")));
        return userRepository.findByEmail(user.getEmail()).switchIfEmpty(userRepository.save(user));
    }

    public Mono<User> findOrCreateUser(String email, String name) {
        User user = new User();
        user.setEmail(email);
        user.setName(name);
        return userRepository.findByEmail(email).switchIfEmpty(userRepository.save(user));
    }

    public Mono<User> findByUserName(String username) {
        return userRepository.findByEmail(username);
    }


    public Mono<User> getCurrentUser() {
        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> {
                    Authentication authentication = securityContext.getAuthentication();
                    if (authentication != null && authentication.isAuthenticated()) {
                        Object principal = authentication.getPrincipal();
                        if (principal instanceof User) {
                            return (User) principal;
                        }
                    }
                    return null;
                });
    }

    public Mono<ContactForm> saveContactForm(ContactForm contactForm) {
        return contactFormRepository.save(contactForm);
    }
}

