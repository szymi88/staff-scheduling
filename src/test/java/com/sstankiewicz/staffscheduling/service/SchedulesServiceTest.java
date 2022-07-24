package com.sstankiewicz.staffscheduling.service;

import com.sstankiewicz.staffscheduling.controller.model.Schedule;
import com.sstankiewicz.staffscheduling.controller.model.UserHours;
import com.sstankiewicz.staffscheduling.repository.ScheduleRepository;
import com.sstankiewicz.staffscheduling.repository.entity.ScheduleEntity;
import com.sstankiewicz.staffscheduling.repository.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.dao.EmptyResultDataAccessException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SchedulesServiceTest {

    @Test
    void deleteSchedule_scheduleExists_returnTrue() {
        var repository = mock(ScheduleRepository.class);
        doNothing().when(repository).deleteById(1L);
        assertTrue(new SchedulesService(repository).deleteSchedule(1L));

        verify(repository, times(1)).deleteById(any());
    }

    @Test
    void deleteSchedule_scheduleNotExists_returnFalse() {
        var repository = mock(ScheduleRepository.class);
        doThrow(EmptyResultDataAccessException.class).when(repository).deleteById(1L);
        assertFalse(new SchedulesService(repository).deleteSchedule(1L));
        verify(repository, times(1)).deleteById(any());
    }

    @Test
    void getSchedules_returnCorrectSchedules() {
        var repository = mock(ScheduleRepository.class);
        when(repository.findAllByUserNameAndWorkDateBetween("user1", LocalDate.of(2000, 1, 1), LocalDate.of(2000, 1, 1)))
                .thenReturn(List.of(ScheduleEntity.builder()
                                            .user(UserEntity.builder().name("user1").build())
                                            .scheduleId(1L)
                                            .build()));

        var result = new SchedulesService(repository).getSchedules("user1", LocalDate.of(2000, 1, 1), LocalDate.of(2000, 1, 1));
        assertThat(result).containsExactly(Schedule.builder().userName("user1").scheduleId(1L).build());
    }

    @Test
    void createSchedule_returnCreatedSchedule() {
        var repository = mock(ScheduleRepository.class);

        when(repository.save(ScheduleEntity.builder()
                                     .user(UserEntity.builder().name("user1").build())
                                     .build()))
                .thenAnswer(invocation -> {
                    var entity = invocation.getArgument(0, ScheduleEntity.class);
                    entity.setScheduleId(1L);
                    return entity;
                });

        var result = new SchedulesService(repository).createSchedule(Schedule.builder().userName("user1").build());

        assertThat(result).isEqualTo(Schedule.builder().userName("user1").scheduleId(1L).build());
        verify(repository, times(1)).save(any());

    }

    @Test
    void createSchedule_scheduleIdInParameter_throwException() {
        var repository = mock(ScheduleRepository.class);
        var service = new SchedulesService(repository);
        var testSchedule = Schedule.builder().scheduleId(1L).userName("user1").build();
        assertThrows(IllegalArgumentException.class, () -> service.createSchedule(testSchedule));
        verify(repository, times(0)).save(any());
    }

    @Test
    void updateSchedule_returnCreatedSchedule() {
        var repository = mock(ScheduleRepository.class);

        when(repository.save(ScheduleEntity.builder()
                                     .scheduleId(1L)
                                     .user(UserEntity.builder().name("user1").build())
                                     .build()))
                .thenAnswer(invocation -> invocation.getArgument(0, ScheduleEntity.class));

        var result = new SchedulesService(repository).updateSchedule(Schedule.builder().userName("user1").scheduleId(1L).build());

        assertThat(result).isEqualTo(Schedule.builder().userName("user1").scheduleId(1L).build());
        verify(repository, times(1)).save(any());
    }

    @Test
    void updateSchedule_noScheduleIdDefined_throwException() {
        var repository = mock(ScheduleRepository.class);
        var service = new SchedulesService(repository);
        var testSchedule = Schedule.builder().userName("user1").build();
        assertThrows(IllegalArgumentException.class, () -> service.updateSchedule(testSchedule));
        verify(repository, times(0)).save(any());
    }

    @Test
    void getUsersHours_returnsHoursSum() {
        var repository = mock(ScheduleRepository.class);
        when(repository.calculateWorkHours(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 1, 1)))
                .thenReturn(List.of(new Object[]{"user1", BigDecimal.valueOf(5)},
                                    new Object[]{"user2", BigDecimal.valueOf(10)}));
        var result = new SchedulesService(repository).getUsersHours(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 1, 1));

        assertThat(result).containsExactly(
                UserHours.builder()
                        .userName("user1")
                        .workingHours(5)
                        .build(),
                UserHours.builder()
                        .userName("user2")
                        .workingHours(10)
                        .build());
    }
}