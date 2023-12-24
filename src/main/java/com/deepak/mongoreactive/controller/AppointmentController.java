package com.deepak.mongoreactive.controller;

import com.deepak.mongoreactive.models.User;
import com.deepak.mongoreactive.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

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

    @PostMapping("/cancelbyphonenumber/{phoneNumber}")
    @Operation(summary = "Cancel appointment by phoneNumber")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Appointments cancelled"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "User does not exist")
    })
    public Mono<User> cancelAppointmentByPhoneNumber(@PathVariable String phoneNumber, @RequestBody List<String> appointmentId) {

        return this.userService.cancelAppointmentByPhoneNumber(phoneNumber, appointmentId);
    }

    @PostMapping("/cancelbyid/{userId}")
    @Operation(summary = "Cancel appointment by userid")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Appointments cancelled"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "User does not exist")
    })
    public Mono<User> cancelAppointmentById(@PathVariable String userId, @RequestBody List<String> appointmentId) {

        return this.userService.cancelAppointmentByUserId(userId, appointmentId);
    }
}
