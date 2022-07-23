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

	public static final String ADMIN_ROLE = "ADMIN";
	public static final String USER_ROLE = "USER";

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
				.authorizeRequests()
				.antMatchers(HttpMethod.GET, "/users/*/schedules").hasAnyRole(ADMIN_ROLE, USER_ROLE)
				.antMatchers("/**").hasRole(ADMIN_ROLE)
				.and().csrf().disable()
				.httpBasic();
		return http.build();
	}
}