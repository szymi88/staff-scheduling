package com.sstankiewicz.staffscheduling.controller;

import com.sstankiewicz.staffscheduling.controller.model.Schedule;
import com.sstankiewicz.staffscheduling.service.SchedulesService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@RestController
@RequestMapping("/users/{userId}/schedules")
public class SchedulesController {

    private final SchedulesService schedulesService;

    public SchedulesController(SchedulesService schedulesService) {
        this.schedulesService = schedulesService;
    }

    @GetMapping
    List<Schedule> getSchedule(@PathVariable String userId, @RequestParam LocalDate from, @RequestParam LocalDate to) {

        if (Period.between(from, to).getYears() >= 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Period over one year");
        }

        try {
            return schedulesService.getSchedules(userId, from, to);
        } catch (SchedulesService.UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    Schedule createSchedule(@PathVariable String userId, @RequestBody Schedule schedule) {

        if (schedule.getScheduleId() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ScheduleId in the body");
        }

        try {
            return schedulesService.createSchedule(userId, schedule);
        } catch (SchedulesService.UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @PutMapping("/{scheduleId}")
    Schedule updateSchedule(@PathVariable String userId, @PathVariable Long scheduleId, @RequestBody Schedule schedule) {

        if (userId == null || !userId.equals(schedule.getUserId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Incorrect userId in the body");
        }

        if (scheduleId == null || !scheduleId.equals(schedule.getScheduleId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Incorrect scheduleId in the body");
        }

        try {
            return schedulesService.updateSchedule(schedule);
        } catch (SchedulesService.UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @DeleteMapping("/{scheduleId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteSchedule(@PathVariable String userId, @PathVariable Long scheduleId, HttpServletResponse response) {
        if (!schedulesService.deleteSchedule(userId, scheduleId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }
}
