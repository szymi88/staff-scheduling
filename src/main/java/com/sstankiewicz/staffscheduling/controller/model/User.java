package com.sstankiewicz.staffscheduling.controller.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Schema(example = "user1")
    @NotBlank(message = "userName is mandatory")
    private String userName;

    @Schema(example = "pass123")
    @NotBlank(message = "password is mandatory")
    private String password;

    @Schema(description = "User's coworkers", example = "[\"user2\", \"user3\"]")
    private Set<String> coworkers;
}