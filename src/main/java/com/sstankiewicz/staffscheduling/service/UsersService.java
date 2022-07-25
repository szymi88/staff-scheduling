package com.sstankiewicz.staffscheduling.service;

import com.sstankiewicz.staffscheduling.config.WebSecurityConfig;
import com.sstankiewicz.staffscheduling.controller.model.User;
import com.sstankiewicz.staffscheduling.repository.UserRepository;
import com.sstankiewicz.staffscheduling.repository.entity.UserEntity;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.stream.Collectors;

@Service
public class UsersService {

    private final UserRepository userRepository;

    private final SchedulesService schedulesService;

    public UsersService(UserRepository userRepository, SchedulesService schedulesService) {
        this.userRepository = userRepository;
        this.schedulesService = schedulesService;
    }

    @Transactional
    public void deleteUser(String user) {
        schedulesService.deleteUsersSchedules(user);
        userRepository.deleteById(user);
    }

    public void updateUser(User user) {

        if (user == null) {
            throw new IllegalArgumentException("User is null");
        }
        try {
            userRepository.save(UserEntity.builder()
                                        .name(user.getUserName())
                                        .password(user.getPassword())
                                        .role(WebSecurityConfig.Role.USER)
                                        .coworkers(user.getCoworkers().stream()
                                                           .map(userName -> UserEntity.builder().name(userName).build())
                                                           .collect(Collectors.toSet()))
                                        .build());
        } catch (EntityNotFoundException e) {
            throw new IllegalArgumentException("Non-existing coworker");
        }
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