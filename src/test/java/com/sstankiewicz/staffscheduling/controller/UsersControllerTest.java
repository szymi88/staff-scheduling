package com.sstankiewicz.staffscheduling.controller;

import com.sstankiewicz.staffscheduling.controller.model.User;
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

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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


    @Test
    void updateUser_expect200() throws Exception {
        var testUser = User.builder().userId("user1").password("123").build();
        when(usersService.updateUser(testUser))
                .thenReturn(true);

        mvc.perform(put("/users/user1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                     {
                                        "userId": "user1",
                                        "password": "123"
                                     }
                                     """))
                .andExpect(status().isOk());

        verify(usersService, times(1)).updateUser(testUser);
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
        when(usersService.deleteUser("user1")).thenReturn(true);

        mvc.perform(delete("/users/user1"))
                .andExpect(status().isNoContent());

        verify(usersService, times(1)).deleteUser("user1");
    }
}