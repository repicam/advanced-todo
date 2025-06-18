package es.myprojects.todo.services;

import es.myprojects.todo.dtos.LoginRequest;
import es.myprojects.todo.dtos.RegisterRequest;
import es.myprojects.todo.models.User;
import es.myprojects.todo.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthServiceTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private JwtService jwtService;
    private AuthenticationConfiguration authenticationConfiguration;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        jwtService = mock(JwtService.class);
        authenticationConfiguration = mock(AuthenticationConfiguration.class);
        authService = new AuthService(userRepository, passwordEncoder, jwtService, authenticationConfiguration);
    }

    @Test
    void register_success() {
        RegisterRequest req = new RegisterRequest("user", "pass", "mail@mail.com");
        when(userRepository.findByUsername("user")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("mail@mail.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("pass")).thenReturn("encoded");
        User saved = User.builder().username("user").email("mail@mail.com").password("encoded").build();
        when(userRepository.save(any(User.class))).thenReturn(saved);

        User result = authService.register(req);

        assertEquals("user", result.getUsername());
        assertEquals("mail@mail.com", result.getEmail());
        assertEquals("encoded", result.getPassword());
    }

    @Test
    void register_whenUsernameExists_throws() {
        RegisterRequest req = new RegisterRequest("user", "pass", "mail@mail.com");
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(new User()));

        assertThrows(IllegalArgumentException.class, () -> authService.register(req));
    }

    @Test
    void register_whenEmailExists_throws() {
        RegisterRequest req = new RegisterRequest("user", "pass", "mail@mail.com");
        when(userRepository.findByUsername("user")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("mail@mail.com")).thenReturn(Optional.of(new User()));

        assertThrows(IllegalArgumentException.class, () -> authService.register(req));
    }

    @Test
    void login_success() throws Exception {
        LoginRequest req = new LoginRequest("user", "pass");
        AuthenticationManager authManager = mock(AuthenticationManager.class);
        when(authenticationConfiguration.getAuthenticationManager()).thenReturn(authManager);
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(User.builder().username("user").password("encoded").build()));
        when(jwtService.generateToken(any())).thenReturn("jwt");

        String token = authService.login(req);

        assertEquals("jwt", token);
        verify(authManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void login_whenAuthFails_throws() throws Exception {
        LoginRequest req = new LoginRequest("user", "pass");
        AuthenticationManager authManager = mock(AuthenticationManager.class);
        when(authenticationConfiguration.getAuthenticationManager()).thenReturn(authManager);
        doThrow(new RuntimeException("fail")).when(authManager).authenticate(any());

        SecurityException ex = assertThrows(SecurityException.class, () -> authService.login(req));
        assertTrue(ex.getMessage().contains("Authentication failed"));
    }

    @Test
    void login_whenUserNotFound_throws() throws Exception {
        LoginRequest req = new LoginRequest("user", "pass");
        AuthenticationManager authManager = mock(AuthenticationManager.class);
        when(authenticationConfiguration.getAuthenticationManager()).thenReturn(authManager);
        when(userRepository.findByUsername("user")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> authService.login(req));
    }

    @Test
    void loadUserByUsername_success() {
        User user = User.builder().username("user").build();
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));

        assertEquals(user, authService.loadUserByUsername("user"));
    }

    @Test
    void loadUserByUsername_notFound_throws() {
        when(userRepository.findByUsername("user")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> authService.loadUserByUsername("user"));
    }
}
