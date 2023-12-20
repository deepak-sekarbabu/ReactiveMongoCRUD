package com.deepak.mongoreactive.controller;

import com.deepak.mongoreactive.models.User;
import com.deepak.mongoreactive.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/user")
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public Mono<User> createUser(@RequestBody User user) {
        LOGGER.info("Received request to create user");
        return userService.saveUser(user);
    }

    @GetMapping
    public Flux<User> getAllUsers() {
        LOGGER.info("Received request to fetch all users");
        return userService.getUsers();
    }

    @GetMapping("/{id}")
    public Mono<User> getUserById(@PathVariable String id) {
        LOGGER.info("Received request to fetch user by ID: {}", id);
        return userService.getUserById(id);
    }

    @PutMapping("/{id}")
    public Mono<User> updateUser(@PathVariable String id, @RequestBody User updatedUser) {
        LOGGER.info("Received request to update user with ID: {}", id);
        return userService.updateUser(id, updatedUser);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deleteUser(@PathVariable String id) {
        LOGGER.info("Received request to delete user with ID: {}", id);
        return userService.deleteUser(id);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception ex) {
        ex.printStackTrace();
        LOGGER.error("An error occurred: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred. Please try again later.");
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<String> handleResponseStatusException(ResponseStatusException ex) {
        LOGGER.error("A ResponseStatusException occurred: {}", ex.getMessage());
        return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
    }
}