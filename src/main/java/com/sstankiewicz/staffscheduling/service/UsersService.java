package com.sstankiewicz.staffscheduling.service;

import com.sstankiewicz.staffscheduling.config.WebSecurityConfig;
import com.sstankiewicz.staffscheduling.controller.model.User;
import com.sstankiewicz.staffscheduling.repository.UserRepository;
import com.sstankiewicz.staffscheduling.repository.entity.UserEntity;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class UsersService  {

    private final UserRepository userRepository;

    public UsersService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void deleteUser(String user) {
        userRepository.deleteById(user);
    }

    public void updateUser(User user) {

        if (user == null) {
            throw new IllegalArgumentException("User is null");
        }

        userRepository.save(UserEntity.builder()
                                    .name(user.getUserId())
                                    .password(user.getPassword())
                                    .role(WebSecurityConfig.Role.USER)
                                    .build());
    }

    @EventListener(ApplicationReadyEvent.class)
    public void ensureAdminUserExists() {
        //TODO move admin credentials to application properties
        userRepository.save(UserEntity.builder()
                                    .name("admin")
                                    .password("admin")
                                    .role(WebSecurityConfig.Role.ADMIN)
                                    .build());
    }
}