package com.sstankiewicz.staffscheduling.repository.entity;

import com.sstankiewicz.staffscheduling.config.WebSecurityConfig;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class UserEntity {
    @Id
    private String name;
    private String password;
    private WebSecurityConfig.Role role;
}
