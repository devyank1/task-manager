package dev.yank.taskmanager.service;

import dev.yank.taskmanager.model.User;
import dev.yank.taskmanager.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Page<User> pageAllUsers(Pageable pageable) {
        logger.info("Fetching all users with pagination: page {}, size {}", pageable.getPageNumber(), pageable.getPageSize());

        return userRepository.findAll(pageable);
    }

    public List<User> listAllUsers() {

        logger.info("Fetching all users without pagination");

        List<User> users = userRepository.findAll();

        if (users.isEmpty()) {
            logger.warn("No users found in the database");
        } else {
            logger.info("Found {} users in database", users.size());
        }

        return users;
    }

    public User findById(Long id) {

        logger.info("Fetching user by ID: " + id);

        return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + id));
    }

    public User saveUser(User user) {

        logger.info("Saving user with name: {}" , user.getName());
        validateUser(user);
        return userRepository.save(user);
    }

    private void validateUser(User user) {

        if (user.getName() == null || user.getName().isEmpty()) {
            throw new IllegalArgumentException("User name cannot be null or empty.");
        }
        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            throw new IllegalArgumentException("Invalid email format.");
        }
    }

    public User updateUser(Long id, User userDetails) {

        logger.info("Updating user by ID: {}", id);

        return userRepository.findById(id).map(
                user -> {

                    user.setName(userDetails.getName() != null ? userDetails.getName() : user.getName());
                    user.setEmail(userDetails.getEmail() != null ? userDetails.getEmail() : user.getEmail());

                    logger.info("Updated user details: {}", user);
                    return userRepository.save(user);
                }).orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + id));
    }

    public void deleteUser(Long id) {

        logger.info("Deleting user with ID: {}", id);

        userRepository.findById(id).ifPresentOrElse(user -> {
            userRepository.delete(user);
            logger.info("Deleted user with ID: {}", id);
        },
                () -> {
            throw new EntityNotFoundException("User not found with ID: " + id);
        });
    }
}
