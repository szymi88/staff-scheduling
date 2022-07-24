package com.sstankiewicz.staffscheduling.repository;

import com.sstankiewicz.staffscheduling.repository.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<UserEntity, String> {
}
