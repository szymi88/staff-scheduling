package com.sstankiewicz.staffscheduling.service;

import com.sstankiewicz.staffscheduling.config.WebSecurityConfig;
import com.sstankiewicz.staffscheduling.controller.model.User;
import com.sstankiewicz.staffscheduling.repository.UserRepository;
import com.sstankiewicz.staffscheduling.repository.entity.UserEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;


class UsersServiceTest {

    @Test
    void testEnsureAdminUserExists_createsAdminUser() {
        var repository = mock(UserRepository.class);
        when(repository.save(UserEntity.builder().name("admin").password("admin").role(WebSecurityConfig.Role.ADMIN).build()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        new UsersService(repository, mock(SchedulesService.class)).ensureAdminUserExists();

        verify(repository, times(1)).save(any());
    }

    @Test
    void testUpdateUser_updatesUser() {
        var repository = mock(UserRepository.class);
        when(repository.save(UserEntity.builder().name("user1").password("123").coworkers(Set.of()).role(WebSecurityConfig.Role.USER).build()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        new UsersService(repository, mock(SchedulesService.class)).updateUser(User.builder().userName("user1").password("123").coworkers(Set.of()).build());

        verify(repository, times(1)).save(any());
    }

    @Test
    void testUpdateUser_throwsIllegalArgumentExceptionOnOnNullUser() {
        UsersService usersService = new UsersService(mock(UserRepository.class), mock(SchedulesService.class));

        Assertions.assertThrows(IllegalArgumentException.class, () -> usersService.updateUser(null));
    }

    @Test
    void testDeleteUser() {
        var repository = mock(UserRepository.class);
        doNothing().when(repository).deleteById("user1");

        new UsersService(repository, mock(SchedulesService.class)).deleteUser("user1");

        verify(repository, times(1)).deleteById(any());
    }

    @Test
    void isCoworker_coworkerFound_returnTrue() {
        var repository = mock(UserRepository.class);
        when(repository.findById("user1")).thenReturn(Optional.of(
                UserEntity.builder()
                        .name("user1")
                        .coworkers(Set.of(
                                UserEntity.builder().name("user2").build(),
                                UserEntity.builder().name("user4").build()))
                        .build()));

        assertTrue(new UsersService(repository, mock(SchedulesService.class)).isCoworker("user1", "user2"));
    }

    @Test
    void isCoworker_coworkerNotFound_returnFalse() {
        var repository = mock(UserRepository.class);
        when(repository.findById("user1")).thenReturn(Optional.of(
                UserEntity.builder()
                        .name("user1")
                        .coworkers(Set.of(
                                UserEntity.builder().name("user3").build(),
                                UserEntity.builder().name("user4").build()))
                        .build()));

        assertFalse(new UsersService(repository, mock(SchedulesService.class)).isCoworker("user1", "user2"));
    }
}