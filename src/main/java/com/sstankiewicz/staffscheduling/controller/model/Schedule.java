package com.sstankiewicz.staffscheduling.controller.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Schedule {

    @Schema(description = "Unique schedule ID", example = "1")
    private Long scheduleId;

    @Schema(description = "User related to the schedule", example = "user1")
    @NotBlank(message = "userName is mandatory")
    private String userName;

    @Schema(description = "Work date for the schedule", example = "2020-01-01")
    @NotNull(message = "workDate is mandatory")
    private LocalDate workDate;

    @Schema(description = "Day's work shift length in hours", example = "8")
    @Min(value = 1, message = "shiftLength can't be less than 24h")
    @Max(value = 24, message = "shiftLength can't be more than 24h")
    private int shiftLength;
}
