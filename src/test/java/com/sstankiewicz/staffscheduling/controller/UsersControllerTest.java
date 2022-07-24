package com.sstankiewicz.staffscheduling.controller;

import com.sstankiewicz.staffscheduling.controller.model.User;
import com.sstankiewicz.staffscheduling.controller.model.UserHours;
import com.sstankiewicz.staffscheduling.service.SchedulesService;
import com.sstankiewicz.staffscheduling.service.UsersService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UsersController.class)
class UsersControllerTest {

    @TestConfiguration
    static class DisableSecurity{
        @Bean
        @Primary
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            http.csrf().disable().authorizeRequests().anyRequest().permitAll();
            return http.build();
        }
    }

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UsersService usersService;

    @MockBean
    private SchedulesService schedulesService;


    @Test
    void updateUser_expect200() throws Exception {
        doNothing().when(usersService).updateUser(User.builder().userName("user1").password("123").build());

        mvc.perform(put("/users/user1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                     {
                                        "userName": "user1",
                                        "password": "123"
                                     }
                                     """))
                .andExpect(status().isOk());

        verify(usersService, times(1)).updateUser(any());
    }

    @Test
    void updateUser_userIdMismatch_expect400() throws Exception {
        mvc.perform(put("/users/user2")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                     {
                                        "userId": "user1",
                                        "password": "123"
                                     }
                                     """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteUser_expect204() throws Exception {
        doNothing().when(usersService).deleteUser("user1");


        mvc.perform(delete("/users/user1"))
                .andExpect(status().isNoContent());

        verify(usersService, times(1)).deleteUser(any());
    }

    @Test
    void getUsersWorkHours_expect200() throws Exception {
        when(schedulesService.getUsersHours(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 1, 2)))
                .thenReturn(List.of(UserHours.builder().userName("user1").workingHours(10).build()));

        mvc.perform(get("/users/working-hours?from=2020-01-01&to=2020-01-02"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("""
                                                  [
                                                     {
                                                         "userName": "user1",
                                                         "workingHours": 10
                                                     }
                                                  ]
                                                  """));

        verify(schedulesService, times(1)).getUsersHours(any(), any());
    }


    @Test
    void getUsersWorkHours_periodOver1yr_expect400() throws Exception {
        mvc.perform(get("/users/working-hours?from=2019-01-01&to=2020-01-01"))
                .andExpect(status().isBadRequest());
    }
}