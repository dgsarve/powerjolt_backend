package com.magnasha.powerjolt.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
public class SpringSecurityConfig
{

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception
	{
		http.authorizeRequests(
						authorizeRequests -> authorizeRequests.requestMatchers("/", "/home", "/api/login", "/oauth2/**","/api/transform").permitAll()
								.anyRequest().authenticated()).oauth2Login(
						oauth2Login -> oauth2Login.defaultSuccessUrl("/api/login/success", true)
															.failureUrl("/api/login/failure"))
				.logout(logout -> logout.logoutSuccessUrl("/").permitAll());
		http.csrf(csrf -> csrf.disable());

		return http.build();
	}

	@Bean
	public SessionRegistry sessionRegistry()
	{
		return new SessionRegistryImpl();
	}
}
