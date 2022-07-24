package com.sstankiewicz.staffscheduling.service;

import com.sstankiewicz.staffscheduling.config.WebSecurityConfig;
import com.sstankiewicz.staffscheduling.controller.model.User;
import com.sstankiewicz.staffscheduling.repository.UserRepository;
import com.sstankiewicz.staffscheduling.repository.entity.UserEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;


class UsersServiceTest {

    @Test
    void testEnsureAdminUserExists_createsAdminUser() {
        var repository = mock(UserRepository.class);
        when(repository.save(UserEntity.builder().name("admin").password("admin").role(WebSecurityConfig.Role.ADMIN).build()))
                     .thenAnswer(invocation -> invocation.getArgument(0));

        new UsersService(repository).ensureAdminUserExists();

        verify(repository, times(1)).save(any());
    }

    @Test
    void testUpdateUser_updatesUser() {
        var repository = mock(UserRepository.class);
        when(repository.save(UserEntity.builder().name("user1").password("123").role(WebSecurityConfig.Role.USER).build()))
                     .thenAnswer(invocation -> invocation.getArgument(0));

        new UsersService(repository).updateUser(User.builder().userName("user1").password("123").build());

        verify(repository, times(1)).save(any());
    }

    @Test
    void testUpdateUser_throwsIllegalArgumentExceptionOnOnNullUser() {
        UsersService usersService = new UsersService(mock(UserRepository.class));

        Assertions.assertThrows(IllegalArgumentException.class, () -> usersService.updateUser(null));
    }

    @Test
    void testDeleteUser() {
        var repository = mock(UserRepository.class);
        doNothing().when(repository).deleteById("user1");

        new UsersService(repository).deleteUser("user1");

        verify(repository, times(1)).deleteById(any());
    }
}