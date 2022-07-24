package com.sstankiewicz.staffscheduling.controller;

import com.sstankiewicz.staffscheduling.controller.model.User;
import com.sstankiewicz.staffscheduling.controller.model.UserHours;
import com.sstankiewicz.staffscheduling.service.SchedulesService;
import com.sstankiewicz.staffscheduling.service.UsersService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UsersController {

    private final UsersService usersService;
    private final SchedulesService schedulesService;

    public UsersController(UsersService usersService, SchedulesService schedulesService) {
        this.usersService = usersService;
        this.schedulesService = schedulesService;
    }

    @PutMapping(value = "/{userName}", consumes = MediaType.APPLICATION_JSON_VALUE)
    void updateUser(@PathVariable String userName, @RequestBody User user) {
        if (userName == null || !userName.equals(user.getUserName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Incorrect userName in the body");
        }
        usersService.updateUser(user);
    }

    @DeleteMapping("/{userName}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteUser(@PathVariable String userName) {
        usersService.deleteUser(userName);
    }

    @GetMapping("/working-hours")
    List<UserHours> getWorkingHours(@RequestParam LocalDate from, @RequestParam LocalDate to) {
        if (Period.between(from, to).getYears() >= 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Period over one year");
        }
        return schedulesService.getUsersHours(from, to);
    }
}
