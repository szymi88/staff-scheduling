package com.sstankiewicz.staffscheduling.service;

import com.sstankiewicz.staffscheduling.config.WebSecurityConfig;
import com.sstankiewicz.staffscheduling.controller.model.User;
import com.sstankiewicz.staffscheduling.repository.UserRepository;
import com.sstankiewicz.staffscheduling.repository.entity.UserEntity;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

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
                                    .name(user.getUserName())
                                    .password(user.getPassword())
                                    .role(WebSecurityConfig.Role.USER)
                                    .coworkers(user.getCoworkers().stream()
                                                       .map(userName -> UserEntity.builder().name(userName).build())
                                                       .collect(Collectors.toSet()))
                                    .build());
    }

    @EventListener(ApplicationReadyEvent.class)
    public void ensureAdminUserExists() {
        //TODO: move admin credentials to application properties
        userRepository.save(UserEntity.builder()
                                    .name("admin")
                                    .password("admin")
                                    .role(WebSecurityConfig.Role.ADMIN)
                                    .build());
    }

    public boolean isCoworker(String userName, String coworkerUserName) {
        return userRepository.findById(userName)
                .stream()
                .flatMap(userEntity -> userEntity.getCoworkers().stream())
                .map(UserEntity::getName)
                .anyMatch(coworkerName -> coworkerName.equals(coworkerUserName));
    }
}