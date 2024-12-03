package dev.yank.taskmanager.servicetest;

import dev.yank.taskmanager.model.Project;
import dev.yank.taskmanager.repository.ProjectRepository;
import dev.yank.taskmanager.service.ProjectService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private ProjectService projectService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindProjectById_Success() {

        Project mockProj = new Project();

        mockProj.setId(1L);
        mockProj.setName("Project");

        when(projectRepository.findById(1L)).thenReturn(Optional.of(mockProj));

        Project project = projectService.findById(1L);

        assertNotNull(project);
        assertEquals("Project", project.getName());

        verify(projectRepository, times(1)).findById(1L);
    }

    @Test
    void testFindProjectById_NotFound() {

        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        Exception e = assertThrows(RuntimeException.class, () -> {
            projectService.findById(1L);
        });

        assertEquals("Project not found with ID: 1", e.getMessage());
        verify(projectRepository, times(1)).findById(1L);
    }

    @Test
    void saveProjectTest() {

        Project mockProj = new Project();

        mockProj.setName("New Proj");

        when(projectRepository.save(mockProj)).thenReturn(mockProj);

        Project savedProj = projectService.saveProject(mockProj);

        assertNotNull(savedProj);
        assertEquals("New Proj", savedProj.getName());

        verify(projectRepository, times(1)).save(mockProj);
    }

    @Test
    void testUpdateProject_Success() {

        Long projId = 1L;

        Project existingProj = new Project();

        existingProj.setId(projId);
        existingProj.setName("Old");

        Project projDetails = new Project();

        projDetails.setName("New");

        Project projUpdate = new Project();

        projUpdate.setId(projId);
        projUpdate.setName("New");

        when(projectRepository.findById(projId)).thenReturn(Optional.of(existingProj));
        when(projectRepository.save(existingProj)).thenReturn(projUpdate);

        Project result = projectService.updateProject(projId, projDetails);

        assertNotNull(result);
        assertNotNull("New", result.getName());

        verify(projectRepository, times(1)).findById(projId);
        verify(projectRepository, times(1)).save(existingProj);
    }

    @Test
    void testUpdateProject_NotFound() {

        Long projId = 1L;

        Project projDetails = new Project();

        projDetails.setName("New");

        when(projectRepository.findById(projId)).thenReturn(Optional.empty());

        Exception e = assertThrows(EntityNotFoundException.class, () -> {
            projectService.updateProject(projId, new Project());
        });

        assertEquals("Project not found with ID: " + projId, e.getMessage());
        verify(projectRepository, times(1)).findById(projId);
        verify(projectRepository, never()).save(any());
    }

    @Test
    void testDeleteProject_Success() {

        Long projId = 1L;
        Project existingProj = new Project();

        existingProj.setId(projId);
        existingProj.setName("Project 1");

        when(projectRepository.findById(projId)).thenReturn(Optional.of(existingProj));
        doNothing().when(projectRepository).delete(existingProj);

        projectService.deleteProject(projId);

        verify(projectRepository, times(1)).findById(projId);
        verify(projectRepository, times(1)).delete(existingProj);
    }

    @Test
    void testDeleteProject_NotFound() {

        Long projId = 1L;

        when(projectRepository.findById(projId)).thenReturn(Optional.empty());

        Exception e = assertThrows(EntityNotFoundException.class, () -> {
            projectService.deleteProject(projId);
        });

        assertEquals("Project not found with ID: " + projId, e.getMessage());
        verify(projectRepository, times(1)).findById(projId);
        verify(projectRepository, times(0)).delete(any());
    }
}
