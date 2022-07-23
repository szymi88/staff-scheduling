package com.sstankiewicz.staffscheduling.service;

import com.sstankiewicz.staffscheduling.controller.model.Schedule;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ScheduleService {
    public boolean deleteSchedule(Long userId, Long scheduleId) {
        return false;
    }

    public List<Schedule> getSchedules(Long userId, LocalDate from, LocalDate to) {
        return null;
    }

    public Schedule createSchedule(Long userId, Schedule schedule) {
        return Schedule.builder()
                .userId(1L)
                .userName("UserName")
                .workDate(LocalDate.of(2020, 1, 1))
                .shiftLength(5)
                .build();
    }

    public Schedule updateSchedule(Schedule schedule) {
        return null;
    }

    public static class UserNotFoundException extends RuntimeException {
        public UserNotFoundException(String message) {
            super(message);
        }
    }
}
