package com.sstankiewicz.staffscheduling.controller;

import com.sstankiewicz.staffscheduling.config.WebSecurityConfig;
import com.sstankiewicz.staffscheduling.controller.model.Schedule;
import com.sstankiewicz.staffscheduling.service.SchedulesService;
import com.sstankiewicz.staffscheduling.service.UsersService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Tag(name = "Schedules", description = "Views and modifies schedules for the users")
@RestController
@RequestMapping("/users/{userName}/schedules")
public class SchedulesController {

    private final SchedulesService schedulesService;
    private final UsersService usersService;

    public SchedulesController(SchedulesService schedulesService, UsersService usersService) {
        this.schedulesService = schedulesService;
        this.usersService = usersService;
    }

    @Operation(summary = "Get user's schedules by period")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of user's schedules", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Period over 1 year"),
            @ApiResponse(responseCode = "403", description = "userName is not within authenticated users coworkers") })
    @GetMapping
    List<Schedule> getSchedule(@Parameter(description = "userName to to be searched", example = "user1") @PathVariable String userName,
                               @Parameter(description = "date to search for workDates after", example = "2020-01-01") @RequestParam LocalDate from,
                               @Parameter(description = "date to search for workDates before", example = "2020-01-10") @RequestParam LocalDate to,
                               HttpServletRequest request) {

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

    @Operation(summary = "Creates schedule")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Schedule created", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Schedule id present in the body"),
            @ApiResponse(responseCode = "403", description = "User doesn't have an ADMIN role"),
            @ApiResponse(responseCode = "404", description = "User doesn't exist") })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    Schedule createSchedule(@Parameter(example = "user1") @PathVariable String userName, @RequestBody @Valid Schedule schedule) {

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

    @Operation(summary = "Updates schedule if it already exists or create it under the specified scheduleId")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Schedule created", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "User or schedule IDs in the body don't match the URL params"),
            @ApiResponse(responseCode = "403", description = "User doesn't have an ADMIN role"),
            @ApiResponse(responseCode = "404", description = "User doesn't exist") })
    @PutMapping("/{scheduleId}")
    Schedule updateSchedule(@Parameter(example = "user1") @PathVariable String userName,
                            @Parameter(example = "1") @PathVariable Long scheduleId,
                            @RequestBody @Valid Schedule schedule) {

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

    @Operation(summary = "Delete schedule")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Schedule deleted"),
            @ApiResponse(responseCode = "400", description = "Schedule doesn't belong to the specified user"),
            @ApiResponse(responseCode = "403", description = "User doesn't have an ADMIN role"),
            @ApiResponse(responseCode = "404", description = "User doesn't exist") })
    @DeleteMapping("/{scheduleId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteSchedule(@Parameter(example = "user1") @PathVariable String userName, @Parameter(example = "1") @PathVariable Long scheduleId) {
        if (!schedulesService.deleteSchedule(userName, scheduleId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }
}
