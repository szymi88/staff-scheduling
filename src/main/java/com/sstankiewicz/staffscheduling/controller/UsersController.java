package com.sstankiewicz.staffscheduling.controller;

import com.sstankiewicz.staffscheduling.controller.model.User;
import com.sstankiewicz.staffscheduling.service.UsersService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/users")
public class UsersController {

    private final UsersService usersService;

    public UsersController(UsersService usersService) {
        this.usersService = usersService;
    }

    @PutMapping(value = "/{userId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    void updateUser(@PathVariable String userId, @RequestBody User user) {
        if (userId == null || !userId.equals(user.getUserId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Incorrect userId in the body");
        }
        usersService.updateUser(user);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteUser(@PathVariable String userId) {
        usersService.deleteUser(userId);
    }
}
