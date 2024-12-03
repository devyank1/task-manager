package dev.yank.taskmanager.servicetest;

import dev.yank.taskmanager.model.User;
import dev.yank.taskmanager.repository.UserRepository;
import dev.yank.taskmanager.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Test find by ID (GET METHOD)
    @Test
    void testFindUserById_Success() {

        User mockUser = new User();

        mockUser.setId(1L);
        mockUser.setName("Steve Rogers");
        mockUser.setEmail("steve@email.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        User user = userService.findById(1L);

        assertNotNull(user);
        assertEquals("Steve Rogers", user.getName());
        assertEquals("steve@email.com", user.getEmail());

        verify(userRepository, times(1)).findById(1L);
    }

    // Test find By ID NOT FOUND (GET METHOD)
    @Test
    void testFindUserById_NotFound() {

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            userService.findById(1L);
        });

        assertEquals("User not found with ID: 1", exception.getMessage());
        verify(userRepository, times(1)).findById(1L);
    }

    // Saving a new user (POST METHOD)
    @Test
    void saveUserTest() {

        User mockUser = new User();

        mockUser.setName("Tony Stark");
        mockUser.setEmail("iron@man.com");

        when(userRepository.save(mockUser)).thenReturn(mockUser);

        User savedUser = userService.saveUser(mockUser);

        assertNotNull(savedUser);
        assertEquals("Tony Stark", savedUser.getName());
        assertEquals("iron@man.com", savedUser.getEmail());
        verify(userRepository, times(1)).save(mockUser);
    }

    // Test to update user (PUT METHOD)
    @Test
    void testUpdateUser_Success() {

        Long userId = 1L;

        User existingUser = new User();

        existingUser.setId(userId);
        existingUser.setName("Old Name");
        existingUser.setEmail("old.email@1.com");

        User userDetails = new User();

        userDetails.setName("New name");
        userDetails.setEmail("new.email@example.com");

        User updatedUser = new User();

        updatedUser.setId(userId);
        updatedUser.setName("New Name");
        updatedUser.setEmail("new.email@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(existingUser)).thenReturn(updatedUser);

        User result = userService.updateUser(userId, userDetails);

        assertNotNull(result);
        assertNotNull("New Name", result.getName());
        assertEquals("new.email@example.com", result.getEmail());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(existingUser);
    }

    // Test to update user unsuccessful (PUT METHOD)
    @Test
    void testUpdateUser_UserNotFound() {

        Long userId = 1L;

        User userDetails = new User();
        userDetails.setName("New Name");
        userDetails.setEmail("new.email@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Exception e = assertThrows(EntityNotFoundException.class, () -> {
            userService.updateUser(userId, new User());
        });

        assertEquals("User not found with ID: " + userId, e.getMessage());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).save(any());
    }

    // Test to deleting user
    @Test
    void testDeleteUser_Success() {

        Long userId = 1L;
        User existingUser = new User();

        existingUser.setId(userId);
        existingUser.setName("John Doe");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        doNothing().when(userRepository).delete(existingUser);

        userService.deleteUser(userId);

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).delete(existingUser);
    }

    @Test
    void testDeleteUser_NotFound() {

        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Exception e = assertThrows(EntityNotFoundException.class, () -> {
            userService.deleteUser(userId);
        });

        assertEquals("User not found with ID: " + userId, e.getMessage());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(0)).delete(any());
    }
}
