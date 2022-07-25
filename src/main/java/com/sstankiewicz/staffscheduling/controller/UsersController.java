package com.sstankiewicz.staffscheduling.controller;

import com.sstankiewicz.staffscheduling.controller.model.User;
import com.sstankiewicz.staffscheduling.controller.model.UserHours;
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

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Tag(name = "Users", description = "Allows ADMIN user to manage users")
@RestController
@RequestMapping("/users")
public class UsersController {

    private final UsersService usersService;
    private final SchedulesService schedulesService;

    public UsersController(UsersService usersService, SchedulesService schedulesService) {
        this.usersService = usersService;
        this.schedulesService = schedulesService;
    }

    @Operation(summary = "Create or update user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated"),
            @ApiResponse(responseCode = "400", description = "User name in path and body don't match"),
            @ApiResponse(responseCode = "403", description = "User doesn't have an ADMIN role")
    })
    @PutMapping(value = "/{userName}", consumes = MediaType.APPLICATION_JSON_VALUE)
    void updateUser(@Parameter(example = "user1") @PathVariable String userName, @RequestBody User user) {
        if (userName == null || !userName.equals(user.getUserName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Incorrect userName in the body");
        }
        try {
            usersService.updateUser(user);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @Operation(summary = "Delete user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted"),
            @ApiResponse(responseCode = "403", description = "User doesn't have an ADMIN role")
    })
    @DeleteMapping("/{userName}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteUser(@Parameter(example = "user1") @PathVariable String userName) {
        usersService.deleteUser(userName);
    }

    @Operation(summary = "Calculates total number of work hours for each user over a given period of time")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns user's work time", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Period over one year"),
            @ApiResponse(responseCode = "403", description = "User doesn't have an ADMIN role")})
    @GetMapping("/working-hours")
    List<UserHours> getWorkingHours(@Parameter(description = "date to include workingHours after", example = "2020-01-01") @RequestParam LocalDate from,
                                    @Parameter(description = "date to include workingHours before", example = "2020-01-15") @RequestParam LocalDate to) {
        if (Period.between(from, to).getYears() >= 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Period over one year");
        }
        return schedulesService.getUsersHours(from, to);
    }
}
