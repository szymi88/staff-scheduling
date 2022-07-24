package com.sstankiewicz.staffscheduling.controller.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Schedule {
    private Long scheduleId;
    private String userName;
    private LocalDate workDate;
    private int shiftLength;
}
