package com.sstankiewicz.staffscheduling.repository.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "schedules")
public class ScheduleEntity {
    @Id
    @GeneratedValue
    private Long scheduleId;

    @ManyToOne
    @JoinColumn(name = "user_name")
    private UserEntity user;
    private LocalDate workDate;
    private int shiftLength;
}
