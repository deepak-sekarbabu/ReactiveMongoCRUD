package com.deepak.mongoreactive.controller;

import com.deepak.mongoreactive.models.User;
import com.deepak.mongoreactive.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/appointment")
@Tag(name = "Appointment", description = "Handles CRUD operations for Appointment")
public class AppointmentController {
    private final UserService userService;

    public AppointmentController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/activebyid/{userId}")
    @Operation(summary = "Get user with active Appointments only by userID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User with appointment information retrieved"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "User does not exist")
    })
    public Mono<User> getUserWithActiveAppointmentsUsingUserId(@PathVariable String userId) {
        return this.userService.getUserWithActiveAppointmentsUsingUserId(userId);
    }

    @GetMapping("/activebyphoneNumber/{phoneNumber}")
    @Operation(summary = "Get user with active Appointments only by phoneNumber")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User with appointment information retrieved"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "User does not exist")
    })
    public Mono<User> getUserWithActiveAppointmentsUsingPhoneNumber(@PathVariable String phoneNumber) {
        return this.userService.getUserWithActiveAppointmentsUsingPhoneNumber(phoneNumber);
    }
}
