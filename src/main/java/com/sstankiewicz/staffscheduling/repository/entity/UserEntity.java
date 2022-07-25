package com.sstankiewicz.staffscheduling.repository.entity;

import com.sstankiewicz.staffscheduling.config.WebSecurityConfig;
import lombok.*;

import javax.persistence.*;
import java.util.Set;

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

    @ManyToMany
    @JoinTable(name="coworkers",
            joinColumns=@JoinColumn(name="name"),
            inverseJoinColumns=@JoinColumn(name="coworkerName")
    )
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<UserEntity> coworkers;

    @ManyToMany(mappedBy="coworkers", cascade = CascadeType.REMOVE)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<UserEntity> coworkerOf;

}
