package com.sstankiewicz.staffscheduling.service;

import com.sstankiewicz.staffscheduling.controller.model.Schedule;
import com.sstankiewicz.staffscheduling.controller.model.UserHours;
import com.sstankiewicz.staffscheduling.repository.ScheduleRepository;
import com.sstankiewicz.staffscheduling.repository.entity.ScheduleEntity;
import com.sstankiewicz.staffscheduling.repository.entity.UserEntity;
import org.hibernate.TransientPropertyValueException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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

        try {
            return mapToModel(scheduleRepository.save(mapToEntity(schedule)));
        } catch (TransientPropertyValueException e) {
            if ("user_name".equals(e.getPropertyName())) {
                throw new UserNotFoundException("User %s doesn't exist".formatted(schedule.getUserName()));
            } else {
                throw e;
            }
        }
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

    public List<UserHours> getUsersHours(LocalDate from, LocalDate to) {
        return scheduleRepository.calculateWorkHours(from, to).stream().map(objects -> UserHours.builder()
                .userName((String) objects[0])
                .workingHours(((BigDecimal)objects[1]).intValue())
                .build()).toList();
    }

    public static class UserNotFoundException extends RuntimeException {
        public UserNotFoundException(String message) {
            super(message);
        }
    }
}
