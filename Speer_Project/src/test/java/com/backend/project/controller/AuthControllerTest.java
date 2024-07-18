package com.backend.project.controller;

import com.backend.project.config.ProjectConfig;
import com.backend.project.enums.Messages;
import com.backend.project.exception.AccessDeniedException;
import com.backend.project.model.SendToken;
import com.backend.project.model.UserAuthDetails;
import com.backend.project.service.AuthService;
import com.backend.project.service.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("test")
@Import({ProjectConfig.class})
@WebMvcTest(AuthController.class)
public class AuthControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ProjectConfig config;

    @MockBean
    private AuthService service;

    @MockBean
    private JwtService jwtService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        when(jwtService.verifyJwts(any())).thenReturn(Messages.TKN_VALD.toString());
    }

    @Test
    @DisplayName("Test signup: Positive")
    public void signUpPositive() throws Exception {
        UserAuthDetails user = new UserAuthDetails("username", "password");
        when(service.signup(any())).thenReturn(Messages.SUCCESS.toString());
        this.mvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(content().string(Messages.SUCCESS.toString()));
    }

    @Test
    @DisplayName("Test signup: Negative")
    public void signUpNegative() throws Exception {
        UserAuthDetails user = new UserAuthDetails("username", "password");
        when(service.signup(any())).thenThrow(new DataIntegrityViolationException("Test"));
        this.mvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Test"));
    }

    @Test
    @DisplayName("Test signup with Throttling")
    public void signUpNegativeThrottle() throws Exception {
        MockHttpSession session = new MockHttpSession();
        UserAuthDetails user = new UserAuthDetails("username", "password");
        when(service.signup(any())).thenReturn(Messages.SUCCESS.toString());

        long bucketCapacity = config.getBucketCapacity() + 1;//One more than the capacity  to test throttling

        for (int i = 0; i < bucketCapacity; ++i) {
            System.out.println("REQ " + i);
            if (i < config.getBucketCapacity()) {
                this.mvc.perform(post("/api/auth/signup")
                                .session(session)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(user)))
                        .andExpect(status().isOk());
            } else {
                this.mvc.perform(post("/api/auth/signup")
                                .session(session)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(user)))
                        .andExpect(status().isTooManyRequests());
            }

        }

    }

    @Test
    @DisplayName("Test login: Positive")
    public void loginPositive() throws Exception {
        UserAuthDetails user = new UserAuthDetails("username", "password");
        when(service.login(any())).thenReturn(Messages.TOKEN.toString());

        this.mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(new SendToken(Messages.TOKEN.toString(), ""))));
    }

    @Test
    @DisplayName("Test login: Negative")
    public void loginNegative() throws Exception {
        UserAuthDetails user = new UserAuthDetails("username", "password");
        when(service.login(any())).thenThrow(new AccessDeniedException(Messages.INVLD_CRED.toString()));

        this.mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(objectMapper.writeValueAsString(new SendToken("", Messages.INVLD_CRED.toString()))));
    }

    @Test
    @DisplayName("Test login with Throttling")
    public void loginNegativeThrottle() throws Exception {
        MockHttpSession session = new MockHttpSession();
        UserAuthDetails user = new UserAuthDetails("username", "password");
        when(service.login(any())).thenReturn(Messages.TOKEN.toString());

        long bucketCapacity = config.getBucketCapacity() + 1;//One more than the capacity  to test throttling

        for (int i = 0; i < bucketCapacity; ++i) {
            if (i < config.getBucketCapacity()) {
                this.mvc.perform(post("/api/auth/login")
                                .session(session)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(user)))
                        .andExpect(status().isOk());
            } else {
                this.mvc.perform(post("/api/auth/login")
                                .session(session)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(user)))
                        .andExpect(status().isTooManyRequests());
            }

        }

    }
}
