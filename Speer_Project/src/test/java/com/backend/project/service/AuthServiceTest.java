package com.backend.project.service;

import com.backend.project.entity.User;
import com.backend.project.enums.Messages;
import com.backend.project.exception.AccessDeniedException;
import com.backend.project.exception.UserNotFoundException;
import com.backend.project.model.UserAuthDetails;
import com.backend.project.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepo;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private UserAuthDetails userAuthDetails;
    private User user;

    @BeforeEach
    void setUp() {
        userAuthDetails = new UserAuthDetails("testUser", "password");
        user = new User("testUser", "encodedPassword");
    }

    @Test
    @DisplayName("Test signup success")
    void signupPositive() {
        when(userRepo.save(any(User.class))).thenReturn(user);

        String result = authService.signup(userAuthDetails);

        assertEquals(Messages.SUCCESS.toString(), result);
        verify(userRepo, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Test signup failure")
    void signupNegative() {
        String msg = "Test";
        when(userRepo.save(any(User.class))).thenThrow(new RuntimeException(msg));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.signup(userAuthDetails));

        assertEquals(msg, exception.getMessage());
        verify(userRepo, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Test login success")
    void loginPositive() {
        when(userRepo.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(encoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtService.createJwts(anyString())).thenReturn("jwtToken");

        String result = authService.login(userAuthDetails);

        assertEquals("jwtToken", result);
        verify(userRepo, times(1)).findByUsername(anyString());
        verify(encoder, times(1)).matches(anyString(), anyString());
    }

    @Test
    @DisplayName("Test login user not found")
    void loginNegative1() {
        when(userRepo.findByUsername(anyString())).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> authService.login(userAuthDetails));

        assertEquals(Messages.NO_USR_FND.toString(), exception.getMessage());
        verify(userRepo, times(1)).findByUsername(anyString());
        verify(encoder, times(0)).matches(anyString(), anyString());
    }

    @Test
    @DisplayName("Test login invalid credentials")
    void loginNegative2() {
        when(userRepo.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(encoder.matches(anyString(), anyString())).thenReturn(false);

        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> authService.login(userAuthDetails));

        assertEquals(Messages.INVLD_CRED.toString(), exception.getMessage());
        verify(userRepo, times(1)).findByUsername(anyString());
        verify(encoder, times(1)).matches(anyString(), anyString());
    }

    @Test
    @DisplayName("Test login failure")
    void loginNegative3() {
        String msg = "JWT creation error";
        when(userRepo.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(encoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtService.createJwts(anyString())).thenThrow(new RuntimeException(msg));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.login(userAuthDetails));

        assertEquals(msg, exception.getMessage());
        verify(userRepo, times(1)).findByUsername(anyString());
        verify(encoder, times(1)).matches(anyString(), anyString());
    }
}
