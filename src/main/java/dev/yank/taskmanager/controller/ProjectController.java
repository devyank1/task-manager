package dev.yank.taskmanager.controller;

import dev.yank.taskmanager.model.Project;
import dev.yank.taskmanager.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    public ResponseEntity<List<Project>> listAllProjects() {

        List<Project> project = projectService.findAllProjects();

        return ResponseEntity.ok(project);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Project> projectById(@PathVariable Long id) {

        Project project = projectService.findById(id);

        return ResponseEntity.ok(project);
    }

    @PostMapping
    public ResponseEntity<Project> createProject(@Valid @RequestBody Project project) {

        Project createProject = projectService.saveProject(project);

        return ResponseEntity.status(HttpStatus.CREATED).body(createProject);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Project> updateProject(@PathVariable Long id, @Valid @RequestBody Project projectDetails) {

        Project project = projectService.updateProject(id, projectDetails);

        return ResponseEntity.ok(project);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {

        projectService.deleteProject(id);

        return ResponseEntity.noContent().build();
    }
}
