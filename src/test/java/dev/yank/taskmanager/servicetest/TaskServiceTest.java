package dev.yank.taskmanager.servicetest;

import dev.yank.taskmanager.model.Task;
import dev.yank.taskmanager.repository.TaskRepository;
import dev.yank.taskmanager.service.TaskService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindTaskById_Success() {

        Task mockTask = new Task();

        mockTask.setId(1L);
        mockTask.setTitle("Payment");
        mockTask.setDescription("Anyone");
        mockTask.setPriority(1);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(mockTask));

        Task task = taskService.getTaskById(1L);

        assertNotNull(task);
        assertEquals("Payment", task.getTitle());
        assertEquals("Anyone", task.getDescription());
        assertEquals(1, task.getPriority());

        verify(taskRepository, times(1)).findById(1L);
    }

    @Test
    void testFindTaskById_NotFound() {

        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        Exception e = assertThrows(RuntimeException.class, () -> {
            taskService.getTaskById(1L);
        });

        assertEquals("Task not found with ID: 1", e.getMessage());
        verify(taskRepository, times(1)).findById(1L);
    }

    @Test
    void saveTaskTest() {

        Task newTask = new Task();

        newTask.setTitle("See my parents");
        newTask.setDescription("Tomorrow");
        newTask.setPriority(2);

        when(taskRepository.save(newTask)).thenReturn(newTask);

        Task savedTask = taskService.saveTasks(newTask);

        assertNotNull(savedTask);
        assertEquals("See my parents", savedTask.getTitle());
        assertEquals("Tomorrow", savedTask.getDescription());
        assertEquals(2, savedTask.getPriority());

        verify(taskRepository, times(1)).save(newTask);
    }

    @Test
    void testUpdateTask_Success() {

        Long taskId = 1L;

        Task existingTask = new Task();

        existingTask.setId(taskId);
        existingTask.setTitle("Old Task Name");
        existingTask.setDescription("Old Description");
        existingTask.setPriority(1);

        Task taskDetails = new Task();

        taskDetails.setTitle("New Title");
        taskDetails.setDescription("New Description");
        taskDetails.setPriority(3);

        Task updateTask = new Task();

        updateTask.setId(taskId);
        updateTask.setTitle("New Title");
        updateTask.setDescription("New Description");
        updateTask.setPriority(3);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(existingTask)).thenReturn(updateTask);

        Task result = taskService.updateTask(taskId, taskDetails);

        assertNotNull(result);
        assertNotNull("New Title", result.getTitle());
        assertEquals("New Description", result.getDescription());
        assertEquals(3, result.getPriority());

        verify(taskRepository, times(1)).findById(taskId);
        verify(taskRepository,times(1)).save(existingTask);
    }

    @Test
    void testUpdateTask_NotFound() {

        Long taskId = 1L;

        Task taskDetails = new Task();
        taskDetails.setTitle("New Title");
        taskDetails.setDescription("New Description");
        taskDetails.setPriority(5);

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        Exception e = assertThrows(EntityNotFoundException.class, () -> {
            taskService.updateTask(taskId, new Task());
        });

        assertEquals("Task not found with ID: " + taskId, e.getMessage());

        verify(taskRepository, times(1)).findById(taskId);
        verify(taskRepository, never()).save(any());
    }

    @Test
    void testDeleteTask_Success() {

        Long taskId = 1L;
        Task existingTask = new Task();

        existingTask.setId(taskId);
        existingTask.setTitle("Walk with my dogs");
        existingTask.setDescription("Tomorrow at 6PM");
        existingTask.setPriority(2);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));
        doNothing().when(taskRepository).delete(existingTask);

        taskService.deleteTask(taskId);

        verify(taskRepository, times(1)).findById(taskId);
        verify(taskRepository, times(1)).delete(existingTask);
    }

    @Test
    void testDeleteTask_NotFound() {

        Long taskId = 1L;

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        Exception e = assertThrows(EntityNotFoundException.class, () -> {
            taskService.deleteTask(taskId);
        });

        assertEquals("Task not found with ID: " + taskId, e.getMessage());

        verify(taskRepository, times(1)).findById(taskId);
        verify(taskRepository, times(0)).delete(any());
    }
}
