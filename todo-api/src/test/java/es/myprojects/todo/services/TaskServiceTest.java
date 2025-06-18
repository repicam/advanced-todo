package es.myprojects.todo.services;

import es.myprojects.todo.dtos.TaskDto;
import es.myprojects.todo.dtos.TaskKafkaDto;
import es.myprojects.todo.exceptions.ResourceNotFoundException;
import es.myprojects.todo.models.Task;
import es.myprojects.todo.models.User;
import es.myprojects.todo.repositories.TaskRepository;
import es.myprojects.todo.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TaskServiceTest {

    private TaskRepository taskRepository;
    private UserRepository userRepository;
    private KafkaTemplate<String, TaskKafkaDto> kafkaTemplate;
    private TaskService taskService;

    @BeforeEach
    void setUp() {
        taskRepository = mock(TaskRepository.class);
        userRepository = mock(UserRepository.class);
        kafkaTemplate = mock(KafkaTemplate.class);
        taskService = new TaskService(taskRepository, userRepository, kafkaTemplate);
    }

    @Test
    void createTask_success() {
        User user = User.builder().id(1L).username("user").email("mail@mail.com").build();
        TaskDto dto = TaskDto.builder().title("t").description("d").build();
        Task saved = Task.builder().id(2L).title("t").description("d").user(user).build();

        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(taskRepository.save(any(Task.class))).thenReturn(saved);

        TaskDto result = taskService.createTask(dto, "user");

        assertEquals("t", result.getTitle());
        verify(kafkaTemplate).send(eq("task-events"), eq("created"), any(TaskKafkaDto.class));
    }

    @Test
    void createTask_userNotFound_throws() {
        when(userRepository.findByUsername("user")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> taskService.createTask(new TaskDto(), "user"));
    }

    @Test
    void getTasksByUserId_success() {
        User user = User.builder().id(1L).username("user").build();
        Task task = Task.builder().id(2L).title("t").user(user).build();
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(taskRepository.findByUserId(1L)).thenReturn(Arrays.asList(task));

        assertEquals(1, taskService.getTasksByUserId("user").size());
    }

    @Test
    void getTasksByUserId_userNotFound_throws() {
        when(userRepository.findByUsername("user")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> taskService.getTasksByUserId("user"));
    }

    @Test
    void getTaskById_success() {
        User user = User.builder().id(1L).username("user").build();
        Task task = Task.builder().id(2L).user(user).build();
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(taskRepository.findById(2L)).thenReturn(Optional.of(task));

        assertNotNull(taskService.getTaskById(2L, "user"));
    }

    @Test
    void getTaskById_taskNotFound_throws() {
        User user = User.builder().id(1L).username("user").build();
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(taskRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> taskService.getTaskById(2L, "user"));
    }

    @Test
    void getTaskById_userNotAuthorized_throws() {
        User user = User.builder().id(1L).username("user").build();
        User other = User.builder().id(2L).username("other").build();
        Task task = Task.builder().id(2L).user(other).build();
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(taskRepository.findById(2L)).thenReturn(Optional.of(task));

        assertThrows(SecurityException.class, () -> taskService.getTaskById(2L, "user"));
    }

    @Test
    void updateTask_success_andSendsKafkaIfCompleted() {
        User user = User.builder().id(1L).username("user").email("mail@mail.com").build();
        Task task = Task.builder().id(2L).user(user).completed(false).build();
        TaskDto dto = TaskDto.builder().title("t").description("d").completed(true).build();
        Task updated = Task.builder().id(2L).user(user).title("t").description("d").completed(true).build();

        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(taskRepository.findById(2L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(updated);

        TaskDto result = taskService.updateTask(2L, dto, "user");

        assertTrue(result.isCompleted());
        verify(kafkaTemplate).send(eq("task-events"), eq("completed"), any(TaskKafkaDto.class));
    }

    @Test
    void updateTask_notCompleted_noKafka() {
        User user = User.builder().id(1L).username("user").email("mail@mail.com").build();
        Task task = Task.builder().id(2L).user(user).completed(false).build();
        TaskDto dto = TaskDto.builder().title("t").description("d").completed(false).build();
        Task updated = Task.builder().id(2L).user(user).title("t").description("d").completed(false).build();

        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(taskRepository.findById(2L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(updated);

        taskService.updateTask(2L, dto, "user");

        verify(kafkaTemplate, never()).send(eq("task-events"), eq("completed"), any(TaskKafkaDto.class));
    }

    @Test
    void deleteTask_success() {
        User user = User.builder().id(1L).username("user").email("mail@mail.com").build();
        Task task = Task.builder().id(2L).user(user).build();
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(taskRepository.findById(2L)).thenReturn(Optional.of(task));

        taskService.deleteTask(2L, "user");

        verify(taskRepository).delete(task);
        verify(kafkaTemplate).send(eq("task-events"), eq("deleted"), any(TaskKafkaDto.class));
    }

    @Test
    void deleteTask_notAuthorized_throws() {
        User user = User.builder().id(1L).username("user").build();
        User other = User.builder().id(2L).username("other").build();
        Task task = Task.builder().id(2L).user(other).build();
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(taskRepository.findById(2L)).thenReturn(Optional.of(task));

        assertThrows(SecurityException.class, () -> taskService.deleteTask(2L, "user"));
    }
}