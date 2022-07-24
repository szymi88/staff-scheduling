package com.sstankiewicz.staffscheduling.controller;

import com.sstankiewicz.staffscheduling.config.WebSecurityConfig;
import com.sstankiewicz.staffscheduling.controller.model.Schedule;
import com.sstankiewicz.staffscheduling.service.SchedulesService;
import com.sstankiewicz.staffscheduling.service.UsersService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@RestController
@RequestMapping("/users/{userName}/schedules")
public class SchedulesController {

    private final SchedulesService schedulesService;
    private final UsersService usersService;

    public SchedulesController(SchedulesService schedulesService, UsersService usersService) {
        this.schedulesService = schedulesService;
        this.usersService = usersService;
    }

    @GetMapping
    List<Schedule> getSchedule(@PathVariable String userName, @RequestParam LocalDate from, @RequestParam LocalDate to, HttpServletRequest request) {

        if (Period.between(from, to).getYears() >= 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Period over one year");
        }
        if (!request.getUserPrincipal().getName().equals(userName)
                && !request.isUserInRole(WebSecurityConfig.Role.ADMIN.name())
                && !usersService.isCoworker(request.getUserPrincipal().getName(), userName)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User %s is not a coworker".formatted(userName));
        }

        try {
            return schedulesService.getSchedules(userName, from, to);
        } catch (SchedulesService.UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    Schedule createSchedule(@PathVariable String userName, @RequestBody Schedule schedule) {

        if (schedule.getScheduleId() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ScheduleId in the body");
        }

        schedule.setUserName(userName);
        try {
            return schedulesService.createSchedule(schedule);
        } catch (SchedulesService.UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @PutMapping("/{scheduleId}")
    Schedule updateSchedule(@PathVariable String userName, @PathVariable Long scheduleId, @RequestBody Schedule schedule) {

        if (userName == null || !userName.equals(schedule.getUserName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Incorrect userName in the body");
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
    void deleteSchedule(@PathVariable String userName, @PathVariable Long scheduleId) {
        if (!schedulesService.deleteSchedule(scheduleId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }
}
