package es.myprojects.todo.services;

import es.myprojects.todo.dtos.TaskDto;
import es.myprojects.todo.dtos.TaskKafkaDto;
import es.myprojects.todo.exceptions.ResourceNotFoundException;
import es.myprojects.todo.models.Task;
import es.myprojects.todo.models.User;
import es.myprojects.todo.repositories.TaskRepository;
import es.myprojects.todo.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final KafkaTemplate<String, TaskKafkaDto> kafkaTemplate;

    private static final String TASK_TOPIC = "task-events";

    public TaskDto createTask(TaskDto taskDto, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        Task task = Task.builder()
                .title(taskDto.getTitle())
                .description(taskDto.getDescription())
                .user(user)
                .build();
        Task savedTask = taskRepository.save(task);

        TaskDto createdTaskDto = convertToDto(savedTask);
        kafkaTemplate.send(TASK_TOPIC, "created", TaskKafkaDto.fromTaskDto(createdTaskDto, user.getEmail()));
        return createdTaskDto;
    }

    public List<TaskDto> getTasksByUserId(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        List<Task> tasks = taskRepository.findByUserId(user.getId());
        return tasks.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public TaskDto getTaskById(Long id, String username) {
        Task task = findTaskByIdAndUsername(id, username);
        return convertToDto(task);
    }

    public TaskDto updateTask(Long id, TaskDto taskDto, String username) {
        Task task = findTaskByIdAndUsername(id, username);

        task.setTitle(taskDto.getTitle());
        task.setDescription(taskDto.getDescription());
        task.setCompleted(taskDto.isCompleted());
        Task updatedTask = taskRepository.save(task);

        TaskDto createdTaskDto = convertToDto(updatedTask);
        if (updatedTask.isCompleted())
            kafkaTemplate.send(TASK_TOPIC, "completed", TaskKafkaDto.fromTaskDto(createdTaskDto, task.getUser().getEmail()));

        return createdTaskDto;
    }

    public void deleteTask(Long id, String username) {
        Task task = findTaskByIdAndUsername(id, username);
        String userEmail = task.getUser().getEmail();
        taskRepository.delete(task);

        kafkaTemplate.send(TASK_TOPIC, "deleted", TaskKafkaDto.builder().id(id).userEmail(userEmail).build());
    }

    private Task findTaskByIdAndUsername(Long id, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));

        if (!task.getUser().getId().equals(user.getId()))
            throw new SecurityException("User not authorized to delete this task");

        return task;
    }

    private TaskDto convertToDto(Task task) {
        return TaskDto.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .completed(task.isCompleted())
                .build();
    }
}
