package com.sstankiewicz.staffscheduling.repository;

import com.sstankiewicz.staffscheduling.repository.entity.ScheduleEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface ScheduleRepository extends CrudRepository<ScheduleEntity, Long> {

    List<ScheduleEntity> findAllByUserNameAndWorkDateBetween(String user, LocalDate from, LocalDate to);

    void deleteAllByUserName(String user);

    @Query(value = "SELECT user_name, SUM(shift_length) from schedules WHERE work_date BETWEEN ?1 AND ?2 GROUP BY user_name;", nativeQuery = true)
    List<Object[]> calculateWorkHours(LocalDate from, LocalDate to);
}
