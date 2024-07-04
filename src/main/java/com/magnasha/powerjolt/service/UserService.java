package com.magnasha.powerjolt.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.magnasha.powerjolt.document.User;
import com.magnasha.powerjolt.repository.UserRepository;

import reactor.core.publisher.Mono;


@Service
public class UserService
{

	@Autowired
	private UserRepository userRepository;

	public Mono<User> findOrCreateUser( OAuth2User principal)
	{
		User user = new User();
		user.setEmail(principal.getAttribute("email"));
		user.setName(principal.getName());
		return userRepository.findByEmail(principal.getAttribute("email")).switchIfEmpty(userRepository.save(user));
	}
}

