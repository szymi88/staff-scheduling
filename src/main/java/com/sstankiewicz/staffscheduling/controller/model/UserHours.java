package com.sstankiewicz.staffscheduling.controller.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserHours {

    @Schema(example = "user1")
    private String userName;

    @Schema(description = "User's accumulated work hours in a given period", example = "50")
    private int workingHours;
}

