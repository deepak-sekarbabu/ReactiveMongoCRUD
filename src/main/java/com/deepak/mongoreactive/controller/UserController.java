package com.deepak.mongoreactive.controller;

import com.deepak.mongoreactive.models.ErrorResponse;
import com.deepak.mongoreactive.models.User;
import com.deepak.mongoreactive.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
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
     * @GetMapping("/{id}") - Gets User by ID.
     * @PutMapping("/{id}") - Updates User by ID from request body.
     * @DeleteMapping("/{id}") - Deletes User by ID.
     * @GetMapping("/getByPhoneNumber/{phoneNumber}") - Gets User by PhoneNumber
     * @GetMapping("/getUserId/{phoneNumber}") - Gets UserId by PhoneNumber
     */

    @PostMapping
    @Operation(summary = "Create a new User with appointment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created"),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "User already exists")
    })
    public Mono<User> createUser(@Valid @RequestBody Mono<User> userDTO) {
        return this.userService.saveUser(userDTO);
    }

    @GetMapping
    @Operation(summary = "Get all users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved")
    })
    public Flux<User> getAllUsers(
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "limit", defaultValue = "10") int limit) {
        return this.userService.getUsers(offset, limit);
    }


    @GetMapping("/{id}")
    @Operation(summary = "Retrieve user by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User retrieved"),
            @ApiResponse(responseCode = "404", description = "User does not exist")
    })
    public Mono<ResponseEntity<User>> getUserById(@Parameter(description = "The userId associated with the user's account") @PathVariable String id) {
        return this.userService.getUserById(id)
                .map(user -> ResponseEntity.ok(user))
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user information by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated"),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "User does not exist"),
            @ApiResponse(responseCode = "409", description = "User update conflict")
    })
    public Mono<User> updateUser(@Parameter(description = "The userId associated with the user's account") @PathVariable String id, @RequestBody Mono<User> updatedUserDTO) {
        return this.userService.updateUserInformation(id, updatedUserDTO);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User deleted"),
            @ApiResponse(responseCode = "404", description = "User does not exist")
    })
    public Mono<Void> deleteUser(@Parameter(description = "The userId associated with the user's account") @PathVariable String id) {
        return this.userService.deleteUser(id);
    }

    @GetMapping("/getByPhoneNumber/{phoneNumber}")
    @Operation(summary = "Get user by phoneNumber")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User retrieved"),
            @ApiResponse(responseCode = "404", description = "User does not exist")
    })
    public Mono<ResponseEntity<User>> getUserByPhoneNumber(@Parameter(description = "The phone number associated with the user's account") @PathVariable String phoneNumber) {
        return this.userService.findByPhoneNumber(phoneNumber)
                .map(user -> ResponseEntity.ok(user))
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    @GetMapping("/getUserId/{phoneNumber}")
    @Operation(summary = "Get userId by phoneNumber")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User Id retrieved"),
            @ApiResponse(responseCode = "404", description = "User does not exist")
    })
    public Mono<ResponseEntity<String>> getUserIdByPhoneNumber(@Parameter(description = "The phone number associated with the user's account") @PathVariable String phoneNumber) {
        return this.userService.findUserIdByPhoneNumber(phoneNumber)
                .map(user -> ResponseEntity.ok(user))
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

}
