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

    @PostMapping
    public Mono<User> createUser(@RequestBody User user) {
        return userService.saveUser(user);
    }

    @GetMapping
    public Flux<User> getAllUsers() {
        return userService.getUsers();
    }

    @GetMapping("/{id}")
    public Mono<User> getUserById(@PathVariable String id) {
        return userService.getUserById(id);
    }

    @PutMapping("/{id}")
    public Mono<User> updateUser(@PathVariable String id, @RequestBody User updatedUser) {
        return userService.updateUser(id, updatedUser);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deleteUser(@PathVariable String id) {
        return userService.deleteUser(id);
    }


}