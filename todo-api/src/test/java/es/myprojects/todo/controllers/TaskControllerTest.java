package es.myprojects.todo.controllers;

import es.myprojects.todo.dtos.TaskDto;
import es.myprojects.todo.services.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TaskControllerTest {

    private TaskService taskService;
    private TaskController controller;

    @BeforeEach
    void setup() {
        taskService = mock(TaskService.class);
        controller = new TaskController(taskService);

        // Mockear el usuario autenticado
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("testuser");
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    void createTask_returnsCreatedTask() {
        TaskDto input = new TaskDto();
        TaskDto created = new TaskDto();
        when(taskService.createTask(input, "testuser")).thenReturn(created);

        ResponseEntity<TaskDto> response = controller.createTask(input);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(created, response.getBody());
        verify(taskService).createTask(input, "testuser");
    }

    @Test
    void createTask_whenServiceThrows_returnsException() {
        TaskDto input = new TaskDto();
        when(taskService.createTask(input, "testuser")).thenThrow(new RuntimeException("fail"));

        assertThrows(RuntimeException.class, () -> controller.createTask(input));
    }

    @Test
    void getTasksForCurrentUser_returnsTasks() {
        List<TaskDto> tasks = Arrays.asList(new TaskDto(), new TaskDto());
        when(taskService.getTasksByUserId("testuser")).thenReturn(tasks);

        ResponseEntity<List<TaskDto>> response = controller.getTasksForCurrentUser();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(tasks, response.getBody());
    }

    @Test
    void getTasksForCurrentUser_returnsEmptyList() {
        when(taskService.getTasksByUserId("testuser")).thenReturn(Collections.emptyList());

        ResponseEntity<List<TaskDto>> response = controller.getTasksForCurrentUser();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void getTaskById_returnsTask() {
        TaskDto task = new TaskDto();
        when(taskService.getTaskById(1L, "testuser")).thenReturn(task);

        ResponseEntity<TaskDto> response = controller.getTaskById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(task, response.getBody());
    }

    @Test
    void getTaskById_whenServiceThrows_returnsException() {
        when(taskService.getTaskById(1L, "testuser")).thenThrow(new RuntimeException("not found"));

        assertThrows(RuntimeException.class, () -> controller.getTaskById(1L));
    }

    @Test
    void updateTask_returnsUpdatedTask() {
        TaskDto input = new TaskDto();
        TaskDto updated = new TaskDto();
        when(taskService.updateTask(1L, input, "testuser")).thenReturn(updated);

        ResponseEntity<TaskDto> response = controller.updateTask(1L, input);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updated, response.getBody());
    }

    @Test
    void updateTask_whenServiceThrows_returnsException() {
        TaskDto input = new TaskDto();
        when(taskService.updateTask(1L, input, "testuser")).thenThrow(new RuntimeException("fail"));

        assertThrows(RuntimeException.class, () -> controller.updateTask(1L, input));
    }

    @Test
    void deleteTask_returnsNoContent() {
        ResponseEntity<Void> response = controller.deleteTask(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(taskService).deleteTask(1L, "testuser");
    }

    @Test
    void deleteTask_whenServiceThrows_returnsException() {
        doThrow(new RuntimeException("fail")).when(taskService).deleteTask(1L, "testuser");

        assertThrows(RuntimeException.class, () -> controller.deleteTask(1L));
    }
}