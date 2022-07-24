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
}
