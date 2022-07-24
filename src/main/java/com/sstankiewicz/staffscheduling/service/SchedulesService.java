package com.sstankiewicz.staffscheduling.service;

import com.sstankiewicz.staffscheduling.controller.model.Schedule;
import com.sstankiewicz.staffscheduling.repository.ScheduleRepository;
import com.sstankiewicz.staffscheduling.repository.entity.ScheduleEntity;
import com.sstankiewicz.staffscheduling.repository.entity.UserEntity;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class SchedulesService {

    private final ScheduleRepository scheduleRepository;

    public SchedulesService(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    public boolean deleteSchedule(Long scheduleId) {
        try {
            scheduleRepository.deleteById(scheduleId);
        } catch (EmptyResultDataAccessException e) {
            return false;

        }
        return true;
    }

    public List<Schedule> getSchedules(String userName, LocalDate from, LocalDate to) {
        return scheduleRepository.findAllByUserNameAndWorkDateBetween(userName, from, to).stream().map(this::mapToModel).toList();
    }

    public Schedule createSchedule(Schedule schedule) {
        if (schedule.getScheduleId() != null) {
            throw new IllegalArgumentException("Schedule with id");
        }
        return mapToModel(scheduleRepository.save(mapToEntity(schedule)));
    }

    private ScheduleEntity mapToEntity(Schedule schedule) {
        return ScheduleEntity.builder()
                .user(UserEntity.builder().name(schedule.getUserName()).build())
                .scheduleId(schedule.getScheduleId())
                .workDate(schedule.getWorkDate())
                .shiftLength(schedule.getShiftLength())
                .build();
    }

    private Schedule mapToModel(ScheduleEntity entity) {
        return Schedule.builder()
                .scheduleId(entity.getScheduleId())
                .userName(entity.getUser().getName())
                .workDate(entity.getWorkDate())
                .shiftLength(entity.getShiftLength())
                .build();
    }

    public Schedule updateSchedule(Schedule schedule) {
        if (schedule.getScheduleId() == null) {
            throw new IllegalArgumentException("Schedule id is missing");
        }
        return mapToModel(scheduleRepository.save(mapToEntity(schedule)));
    }

    public static class UserNotFoundException extends RuntimeException {
        public UserNotFoundException(String message) {
            super(message);
        }
    }
}
