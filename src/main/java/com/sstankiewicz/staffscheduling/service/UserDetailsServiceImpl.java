package com.sstankiewicz.staffscheduling.service;

import com.sstankiewicz.staffscheduling.repository.UserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        return userRepository.findById(username).map(userEntity -> User.withDefaultPasswordEncoder()
                .username(userEntity.getName())
                .password(userEntity.getPassword())
                .roles(userEntity.getRole().name())
                .build()).orElse(null);
    }
}