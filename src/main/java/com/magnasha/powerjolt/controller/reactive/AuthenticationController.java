package com.magnasha.powerjolt.controller.reactive;

/**
 * @Author Gnana Prakasam Duraisamy
 * @Date 04.07.24 14:46
 * @Version 1.0
 */


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.magnasha.powerjolt.service.UserService;

import reactor.core.publisher.Mono;


@RestController
@RequestMapping("/api")
public class AuthenticationController
{
	@Autowired
	UserService userService;
	
	@GetMapping("/login/success")
	public Mono<OAuth2User> loginSuccess(@AuthenticationPrincipal OAuth2User principal)
	{
		userService.findOrCreateUser(principal);
		//create a user session and user no needs to login for another 180 days
		return Mono.just(principal);
	}

	@GetMapping("/login/failure")
	public Mono<String> loginFailure()
	{
		return Mono.just("Login failed");
	}

	@GetMapping("/user")
	public Mono<OAuth2User> getUser(@AuthenticationPrincipal OAuth2User principal)
	{
		return Mono.justOrEmpty(principal);
	}
}
