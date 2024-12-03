package dev.yank.taskmanager.service;

import dev.yank.taskmanager.model.Project;
import dev.yank.taskmanager.repository.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectService {

    private static final Logger logger = LoggerFactory.getLogger(ProjectService.class);

    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public Page<Project> pageAllProjects(Pageable pageable) {

        logger.info("Fetching all projects with pagination: page {}, size {}", pageable.getPageNumber(), pageable.getPageSize());

        return projectRepository.findAll(pageable);
    }

    public List<Project> findAllProjects() {
        logger.info("Fetching all projects without pagination.");

        List<Project> projs = projectRepository.findAll();

        if (projs.isEmpty()) {
            logger.warn("No projects found in the database.");
        } else {
            logger.info("Found {} in database", projs.size());
        }

        return projs;
    }

    public Project findById(Long id) {

        logger.info("Fetching project with ID: ", id);

        return projectRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Project not found with ID: " + id));
    }

    public Project saveProject(Project project) {

        logger.info("Saving project with name: {} ", project.getName());
        validateProject(project);
        return projectRepository.save(project);
    }

    private void validateProject(Project project) {

        if (project == null || project.getName().isEmpty()) {
            throw new IllegalArgumentException("Project name cannot be null or empty.");
        }
    }

    public Project updateProject(Long id, Project projectDetails) {

        logger.info("Updating project with ID: {}", id);

        return projectRepository.findById(id).map(proj -> {
            proj.setName(proj.getName() != null ? projectDetails.getName() : proj.getName());
            proj.setTasks(proj.getTasks() != null ? projectDetails.getTasks() : proj.getTasks());

            logger.info("Updated projects details: {}" , proj);
            return projectRepository.save(proj);
        }).orElseThrow(() -> new EntityNotFoundException("Project not found with ID: " + id));
    }

    public void deleteProject(Long id) {

        logger.info("Deleting project with ID: {}", id);

        projectRepository.findById(id).ifPresentOrElse(projs -> {
            projectRepository.delete(projs);
            logger.info("Deleted project with ID: {}", projs);
        },
                () -> {
            throw new EntityNotFoundException("Project not found with ID: " + id);
                });
    }
}
