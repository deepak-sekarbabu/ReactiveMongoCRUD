package com.deepak.mongoreactive.controller;

import com.deepak.mongoreactive.models.User;
import com.deepak.mongoreactive.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/users")
@Tag(name = "User", description = "Handles CRUD operations for User resources")
@Validated
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
     *             @GetMapping("/{id}") - Gets User by ID.
     *             @PutMapping("/{id}") - Updates User by ID from request body.
     *             @DeleteMapping("/{id}") - Deletes User by ID.
     *             @GetMapping("/getByPhoneNumber/{phoneNumber} - Gets User by
     *             PhoneNumber
     */

    @PostMapping
    @Operation(summary = "Create a new User")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "409", description = "User already exists")
    })
    public Mono<User> createUser(@Valid @RequestBody User user) {
        return this.userService.saveUser(user);
    }

    @GetMapping
    @Operation(summary = "Get all users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved")
    })
    public Flux<User> getAllUsers() {
        return this.userService.getUsers();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Retrieve user by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User retrieved"),
            @ApiResponse(responseCode = "404", description = "User does not exist")
    })
    public Mono<User> getUserById(@PathVariable String id) {
        return this.userService.getUserById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated"),
            @ApiResponse(responseCode = "404", description = "User does not exist"),
            @ApiResponse(responseCode = "409", description = "User update conflict")
    })
    public Mono<User> updateUser(@PathVariable String id, @RequestBody User updatedUser) {
        return this.userService.updateUser(id, updatedUser);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User deleted"),
            @ApiResponse(responseCode = "404", description = "User does not exist")
    })
    public Mono<Void> deleteUser(@PathVariable String id) {
        return this.userService.deleteUser(id);
    }

    @GetMapping("/getByPhoneNumber/{phoneNumber}")
    @Operation(summary = "Get user by phoneNumber")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User retrieved"),
            @ApiResponse(responseCode = "404", description = "User does not exist")
    })
    public Mono<User> getUserByPhoneNumber(@PathVariable String phoneNumber) {
        return this.userService.findByPhoneNumber(phoneNumber);
    }

}