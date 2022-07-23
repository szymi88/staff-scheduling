package com.sstankiewicz.staffscheduling.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import static com.sstankiewicz.staffscheduling.config.WebSecurityConfig.ADMIN_ROLE;
import static com.sstankiewicz.staffscheduling.config.WebSecurityConfig.USER_ROLE;

@Service
public class UsersService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) {
        //TODO: move to DB
        var admin =
                User.withDefaultPasswordEncoder()
                        .username("admin")
                        .password("admin")
                        .roles(ADMIN_ROLE)
                        .build();

        var user =
                User.withDefaultPasswordEncoder()
                        .username("user")
                        .password("user")
                        .roles(USER_ROLE)
                        .build();

        if ("admin".equals(username)) {
            return admin;
        }
        if ("user".equals(username)) {
            return user;
        } else {
            return null;
        }
    }
}