package com.sstankiewicz.staffscheduling.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

	public enum Role {
		ADMIN,
		USER
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
				.authorizeRequests()
				.antMatchers(HttpMethod.GET, "/users/*/schedules").hasAnyRole(Role.ADMIN.name(), Role.USER.name())
				.antMatchers("/**").hasRole(Role.ADMIN.name())
				.and().csrf().disable()
				.httpBasic();
		return http.build();
	}
}