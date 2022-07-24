package com.sstankiewicz.staffscheduling.controller.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserHours {
    private String userName;
    private int workingHours;
}

