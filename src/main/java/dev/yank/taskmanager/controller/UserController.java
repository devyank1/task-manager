package dev.yank.taskmanager.controller;

import dev.yank.taskmanager.model.User;
import dev.yank.taskmanager.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Return a list all users with ResponseEntity calling the HTTP status '200 OK'.
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {

        List<User> users = userService.listAllUsers();
        return ResponseEntity.ok(users);
    }

    // Return any user find by ID and with HTTP status '200 OK'.
    @GetMapping("/{id}")
    public ResponseEntity<User> findUsersById(@PathVariable Long id) {

        User user = userService.findById(id);

        return ResponseEntity.ok(user);
    }

    // Return a '201 CREATED' with Response Entity and create a new user.
    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {

        User createdUser = userService.saveUser(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    // Return an updated user with '200 OK'.
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @Valid @RequestBody User userDetails) {

        User updateUser = userService.updateUser(id, userDetails);

        return ResponseEntity.ok(updateUser);
    }

    // Return a deleted user in '204 NO CONTENT'.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {

    userService.deleteUser(id);

    return ResponseEntity.noContent().build();
    }
}
