package com.sstankiewicz.staffscheduling.controller;

import com.sstankiewicz.staffscheduling.controller.model.Schedule;
import com.sstankiewicz.staffscheduling.service.SchedulesService;
import com.sstankiewicz.staffscheduling.service.UsersService;
import org.junit.jupiter.api.Tag;
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

@WebMvcTest(SchedulesController.class)

class SchedulesControllerTest {

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
    private SchedulesService schedulesService;

    @Test
    void getSchedules_returnsResult() throws Exception {
        when(schedulesService.getSchedules("user1", LocalDate.of(2020, 1, 1), LocalDate.of(2020, 1, 2)))
                .thenReturn(List.of(Schedule.builder()
                                            .userId("user1")
                                            .scheduleId(2L)
                                            .userName("UserName")
                                            .workDate(LocalDate.of(2020, 1, 1))
                                            .shiftLength(5)
                                            .build()));

        mvc.perform(get("/users/user1/schedules?from=2020-01-01&to=2020-01-02"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("""
                                                  [
                                                    {
                                                        "scheduleId": 2,
                                                        "userId": "user1",
                                                        "userName": "UserName",
                                                        "workDate": "2020-01-01",
                                                        "shiftLength": 5
                                                    }
                                                  ]
                                                  """));
    }

    @Test
    void getSchedules_noSchedulesFound_returnsEmptyList() throws Exception {
        when(schedulesService.getSchedules("user1", LocalDate.of(2020, 1, 1), LocalDate.of(2020, 1, 2)))
                .thenReturn(List.of());

        mvc.perform(get("/users/user1/schedules?from=2020-01-01&to=2020-01-02"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("[]"));
    }

    @Test
    void getSchedules_userDoesNotExist_return404() throws Exception {
        when(schedulesService.getSchedules("user1", LocalDate.of(2020, 1, 1), LocalDate.of(2020, 1, 2)))
                .thenThrow(new SchedulesService.UserNotFoundException("User doesn't exist"));

        mvc.perform(get("/users/user1/schedules?from=2020-01-01&to=2020-01-02"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getSchedules_periodOver1yr_return400() throws Exception {
        mvc.perform(get("/users/user1/schedules?from=2019-01-01&to=2020-01-01"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createSchedule_createsSchedule_returnsCreatedResourceWithId_respondsWith201() throws Exception {
        when(schedulesService.createSchedule("user1", Schedule.builder()
                .userName("UserName")
                .workDate(LocalDate.of(2020, 1, 1))
                .shiftLength(5)
                .build()))
                .then(invocation -> {
                    var schedule = invocation.getArgument(1, Schedule.class);
                    schedule.setScheduleId(2L);
                    schedule.setUserId("user1");
                    return schedule;
                });

        mvc.perform(post("/users/user1/schedules")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                             {
                                               "userName": "UserName",
                                               "workDate": "2020-01-01",
                                               "shiftLength": 5
                                             }
                                             """))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("""
                                                      {
                                                      "scheduleId": 2,
                                                      "userId": "user1",
                                                      "userName": "UserName",
                                                      "workDate": "2020-01-01",
                                                      "shiftLength": 5
                                                      }
                                                  """));
    }

    @Test
    void createSchedule_scheduleIdInTheBody_return400() throws Exception {
        mvc.perform(post("/users/user1/schedules")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                             {
                                               "scheduleId": 1,
                                               "userName": "UserName",
                                               "workDate": "2020-01-01",
                                               "shiftLength": 5
                                             }
                                             """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createSchedule_userDoesNotExist_return404() throws Exception {
        when(schedulesService.createSchedule(eq("user1"), any()))
                .thenThrow(new SchedulesService.UserNotFoundException("User doesn't exist"));

        mvc.perform(post("/users/user1/schedules")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                             {
                                               "userName": "UserName",
                                               "workDate": "2020-01-01",
                                               "shiftLength": 5
                                             }
                                             """))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateSchedule_updateResource_respondsWith200() throws Exception {
        when(schedulesService.updateSchedule(Schedule.builder()
                                                    .userId("user1")
                                                    .scheduleId(2L)
                                                    .userName("UserName")
                                                    .workDate(LocalDate.of(2020, 1, 1))
                                                    .shiftLength(5)
                                                    .build()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        mvc.perform(put("/users/user1/schedules/2")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                             {
                                               "scheduleId": 2,
                                               "userId": "user1",
                                               "userName": "UserName",
                                               "workDate": "2020-01-01",
                                               "shiftLength": 5
                                              }
                                               """))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    ///TODO: PUT 201 if created

    @Test
    void updateSchedule_wrongScheduleIdInBody_respondWith400() throws Exception {
        mvc.perform(put("/users/user1/schedules/2")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                             {
                                              "scheduleId": 3,
                                              "userId": "user1",
                                              "userName": "UserName",
                                              "workDate": "2020-01-01",
                                              "shiftLength": 5
                                             }"""))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateSchedule_userDoesNotExist_respondWith404() throws Exception {
        when(schedulesService.updateSchedule(Schedule.builder()
                                                    .userId("user1")
                                                    .scheduleId(2L)
                                                    .userName("UserName")
                                                    .workDate(LocalDate.of(2020, 1, 1))
                                                    .shiftLength(5)
                                                    .build()))
                .thenThrow(new SchedulesService.UserNotFoundException("User doesn't exist"));

        mvc.perform(put("/users/user1/schedules/2")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                             {
                                              "scheduleId": 2,
                                              "userId": "user1",
                                              "userName": "UserName",
                                              "workDate": "2020-01-01",
                                              "shiftLength": 5
                                             }"""))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateSchedule_wrongUserIdInBody_respondWith400() throws Exception {
        mvc.perform(put("/users/user1/schedules/2")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                             {
                                              "scheduleId": 2,
                                              "userId": "user2",
                                              "userName": "UserName",
                                              "workDate": "2020-01-01",
                                              "shiftLength": 5
                                             }"""))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteSchedules_deleteExisting_returns204() throws Exception {
        when(schedulesService.deleteSchedule("user1", 2L)).thenReturn(true);

        mvc.perform(delete("/users/user1/schedules/2"))
                .andExpect(status().isNoContent());

        verify(schedulesService, times(1)).deleteSchedule("user1", 2L);
    }

    @Test
    void deleteSchedules_notExisting_return404() throws Exception {
        when(schedulesService.deleteSchedule("user1", 2L)).thenReturn(false);

        mvc.perform(delete("/users/user1/schedules/2"))
                .andExpect(status().isNotFound());

        verify(schedulesService, times(1)).deleteSchedule("user1", 2L);
    }

    /*

        GET for resource


    /*
    TODO
    this should be under users
    * Can order users list by accumulated work hours per arbitrary period (up to 1
year).
    * */
}