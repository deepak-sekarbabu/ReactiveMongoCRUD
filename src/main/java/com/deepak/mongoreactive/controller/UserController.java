package com.deepak.mongoreactive.controller;

import com.deepak.mongoreactive.models.User;
import com.deepak.mongoreactive.service.UserService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * User REST controller.
     * Handles CRUD operations for User resources.
     * <p>
     * Mappings:
     *
     * @PostMapping - Creates a new User from request body.
     * @GetMapping - Gets all Users.
     * @GetMapping("/{id}") - Gets User by ID.
     * @PutMapping("/{id}") - Updates User by ID from request body.
     * @DeleteMapping("/{id}") - Deletes User by ID.
     * @GetMapping("/getByPhoneNumber/{phoneNumber} - Gets User by PhoneNumber
     */
    @PostMapping
    public Mono<User> createUser(@RequestBody User user) {
        return this.userService.saveUser(user);
    }

    @GetMapping
    public Flux<User> getAllUsers() {
        return this.userService.getUsers();
    }

    @GetMapping("/{id}")
    public Mono<User> getUserById(@PathVariable String id) {
        return this.userService.getUserById(id);
    }

    @PutMapping("/{id}")
    public Mono<User> updateUser(@PathVariable String id, @RequestBody User updatedUser) {
        return this.userService.updateUser(id, updatedUser);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deleteUser(@PathVariable String id) {
        return this.userService.deleteUser(id);
    }

    @GetMapping("/getByPhoneNumber/{phoneNumber}")
    public Mono<User> getUserByPhoneNumber(@PathVariable String phoneNumber) {
        return this.userService.findByPhoneNumber(phoneNumber);
    }

}