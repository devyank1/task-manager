package dev.yank.taskmanager.service;

import dev.yank.taskmanager.model.Task;
import dev.yank.taskmanager.repository.TaskRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Page<Task> pageAllTasks(Pageable pageable) {

        logger.info("Fetching all tasks with pagination: page {}, size {}", pageable.getPageNumber(), pageable.getPageSize());

        return taskRepository.findAll(pageable);
    }

    public List<Task> findAllTasks() {

        logger.info("Fetching all tasks without pagination");

        List<Task> tasks = taskRepository.findAll();

        if (tasks.isEmpty()) {
            logger.warn("No tasks found in the database");
        } else {
            logger.info("Found {} users in database", tasks.size());
        }

        return tasks;
    }

    public Task getTaskById(Long id) {

        logger.info("Fetching task with ID: {}", id);

        return taskRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Task not found with ID: " + id));
    }

    public Task saveTasks(Task task) {

        logger.info("Saving task with title: {}", task.getTitle());
        validateTask(task);
        return taskRepository.save(task);
    }

    private void validateTask(Task task) {

        if (task.getTitle() == null || task.getTitle().isEmpty()) {
            throw new IllegalArgumentException("Task title cannot be null or empty.");
        } if (task.getPriority() == null || task.getPriority().describeConstable().isEmpty()) {
            throw new IllegalArgumentException("Task priority cannot be null or empty.");
        }
    }

    public Task updateTask(Long id, Task taskDetails) {

        logger.info("Updating task with ID: {}", id);

        return taskRepository.findById(id).map(
                task -> {
                    task.setTitle(task.getTitle() != null ? taskDetails.getTitle() : task.getTitle());
                    task.setDescription(task.getDescription() != null ? taskDetails.getDescription() : task.getDescription());
                    task.setStatus(task.getStatus() != null ? taskDetails.getStatus() : task.getStatus());
                    task.setPriority(task.getPriority() != null ? taskDetails.getPriority() : task.getPriority());
                    task.setAssignedTo(task.getAssignedTo() != null ? taskDetails.getAssignedTo() : task.getAssignedTo());
                    task.setProject(task.getProject() != null ? taskDetails.getProject() : task.getProject());

                    logger.info("Updated task details: {}", task);
                    return taskRepository.save(task);
                }).orElseThrow(() -> new EntityNotFoundException("Task not found with ID: " + id));
    }

    public void deleteTask(Long id) {

        logger.info("Deleting task with ID: {}" , id);

        taskRepository.findById(id).ifPresentOrElse(task -> {
            taskRepository.delete(task);
            logger.info("Deleted task with ID: {}" , id);
        },
                () ->{
            throw new EntityNotFoundException("Task not found with ID: " + id);
                });
    }
}
