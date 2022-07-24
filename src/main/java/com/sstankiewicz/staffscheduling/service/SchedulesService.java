package com.sstankiewicz.staffscheduling.service;

import com.sstankiewicz.staffscheduling.controller.model.Schedule;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class SchedulesService {
    //TODO
    public boolean deleteSchedule(String userId, Long scheduleId) {
        return false;
    }

    public List<Schedule> getSchedules(String userId, LocalDate from, LocalDate to) {
        return null;
    }

    public Schedule createSchedule(String userId, Schedule schedule) {
        return Schedule.builder()
                .userId("user1")
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
