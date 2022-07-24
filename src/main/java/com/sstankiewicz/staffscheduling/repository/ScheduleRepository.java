package com.sstankiewicz.staffscheduling.repository;

import com.sstankiewicz.staffscheduling.repository.entity.ScheduleEntity;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface ScheduleRepository extends CrudRepository<ScheduleEntity, Long> {

    List<ScheduleEntity> findAllByUserNameAndWorkDateBetween(String user, LocalDate from, LocalDate to);
}
