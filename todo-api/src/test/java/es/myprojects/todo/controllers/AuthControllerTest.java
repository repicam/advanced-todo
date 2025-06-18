package es.myprojects.todo.controllers;

import es.myprojects.todo.dtos.JwtResponse;
import es.myprojects.todo.dtos.LoginRequest;
import es.myprojects.todo.dtos.RegisterRequest;
import es.myprojects.todo.services.AuthService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthControllerTest {

    @Test
    void registerUser_returnsCreatedWithToken() {
        AuthService authService = mock(AuthService.class);
        RegisterRequest registerRequest = new RegisterRequest("user", "mail@mail.com", "pass");
        when(authService.login(any(LoginRequest.class))).thenReturn("token");

        AuthController controller = new AuthController(authService);
        ResponseEntity<JwtResponse> response = controller.registerUser(registerRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("token", response.getBody().getToken());
        verify(authService).register(registerRequest);
        ArgumentCaptor<LoginRequest> captor = ArgumentCaptor.forClass(LoginRequest.class);
        verify(authService).login(captor.capture());
        assertEquals("user", captor.getValue().getUsername());
    }

    @Test
    void registerUser_whenServiceThrows_returnsException() {
        AuthService authService = mock(AuthService.class);
        RegisterRequest registerRequest = new RegisterRequest("user", "mail@mail.com", "pass");
        doThrow(new RuntimeException("fail")).when(authService).register(registerRequest);

        AuthController controller = new AuthController(authService);
        assertThrows(RuntimeException.class, () -> controller.registerUser(registerRequest));
    }

    @Test
    void authenticateUser_returnsOkWithToken() {
        AuthService authService = mock(AuthService.class);
        LoginRequest loginRequest = new LoginRequest("user", "pass");
        when(authService.login(loginRequest)).thenReturn("token2");

        AuthController controller = new AuthController(authService);
        ResponseEntity<JwtResponse> response = controller.authenticateUser(loginRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("token2", response.getBody().getToken());
        verify(authService).login(loginRequest);
    }

    @Test
    void authenticateUser_whenServiceThrows_returnsException() {
        AuthService authService = mock(AuthService.class);
        LoginRequest loginRequest = new LoginRequest("user", "pass");
        when(authService.login(loginRequest)).thenThrow(new RuntimeException("login error"));

        AuthController controller = new AuthController(authService);
        assertThrows(RuntimeException.class, () -> controller.authenticateUser(loginRequest));
    }
}