package com.sstankiewicz.staffscheduling.controller;

import com.sstankiewicz.staffscheduling.service.UsersService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UsersController {

    private final UsersService usersService;

    public UsersController(UsersService usersService) {
        this.usersService = usersService;
    }

    @GetMapping("/{userId}")
    String getUser(@PathVariable Long userId) {
        return "GET_USER" + userId;
    }

    @PostMapping()
    String createUser() {
        return "POST_USER";
    }

    @PutMapping("/{userId}")
    String updateUser(@PathVariable Long userId) {
        return "UPDATE_USER" + userId;
    }

    @DeleteMapping("/{userId}")
    String deleteUser(@PathVariable Long userId) {
        return "DELETE_USER" + userId;
    }
}
