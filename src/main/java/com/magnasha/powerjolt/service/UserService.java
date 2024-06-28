package com.magnasha.powerjolt.service;

import com.magnasha.powerjolt.document.User;
import com.magnasha.powerjolt.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public Mono<User> findOrCreateUser(String email, String name) {
        User user = new User();
        user.setEmail(email);
        user.setName(name);
        return userRepository.findByEmail(email)
                .switchIfEmpty(userRepository.save(user));
    }
}

